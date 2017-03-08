package com.tananaev.calculator;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.quicksettings.Tile;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application {

    private static final int NOTIFICATION_ID = 1;
    private static final int REQUEST_DISMISS = 1;
    private static final String EXTRA_ID = "id";
    private static final String BROADCAST_BUTTON = "CalculatorButton";
    private static final String BROADCAST_DISMISS = "CalculatorDismiss";
    private static final int DECIMAL_PRECISION = 12;

    private ClipboardManager clipboardManager;
    private NotificationManager notificationManager;
    private RemoteViews remoteViewsSmall;
    private RemoteViews remoteViewsLarge;
    private NotificationCompat.Builder notificationBuilder;
    private Tile tile;
    private boolean showing;

    private String value = "";

    @SuppressLint("NewApi")
    public void setTile(Tile tile) {
        this.tile = tile;
        if (tile != null) {
            tile.setState(showing ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            tile.updateTile();
        }
    }

    @SuppressLint("NewApi")
    public void setShowing(boolean showing) {
        this.showing = showing;
        if (this.tile != null) {
            this.tile.setState(showing ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
            this.tile.updateTile();
        }
    }

    public void showNotification() {
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViewsSmall = new RemoteViews(getPackageName(), R.layout.view_calculator_small);
        remoteViewsLarge = new RemoteViews(getPackageName(), R.layout.view_calculator_large);

        List<Integer> buttons = Arrays.asList(
                R.id.digit_0, R.id.digit_1, R.id.digit_2, R.id.digit_3, R.id.digit_4,
                R.id.digit_5, R.id.digit_6, R.id.digit_7, R.id.digit_8, R.id.digit_9,
                R.id.button_clear, R.id.button_delete, R.id.button_dot, R.id.button_equal,
                R.id.button_divide, R.id.button_multiply, R.id.button_subtract, R.id.button_add,
                R.id.button_copy, R.id.button_paste);

        for (int viewId : buttons) {
            remoteViewsSmall.setOnClickPendingIntent(viewId, PendingIntent.getBroadcast(
                    this, viewId, new Intent(BROADCAST_BUTTON).putExtra(EXTRA_ID, viewId), 0));
            remoteViewsLarge.setOnClickPendingIntent(viewId, PendingIntent.getBroadcast(
                    this, viewId, new Intent(BROADCAST_BUTTON).putExtra(EXTRA_ID, viewId), 0));
        }

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker(getString(R.string.notification_title))
                .setContent(remoteViewsSmall)
                .setCustomBigContentView(remoteViewsLarge)
                .setDeleteIntent(PendingIntent.getBroadcast(
                        this, REQUEST_DISMISS, new Intent(BROADCAST_DISMISS), 0));

        remoteViewsSmall.setTextViewText(R.id.view_display, value);
        remoteViewsLarge.setTextViewText(R.id.view_display, value);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        setShowing(true);
        startService(new Intent(this, BackgroundService.class));
    }

    public void hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
        setShowing(false);
        stopService(new Intent(this, BackgroundService.class));
    }

    public void addCharacter(int buttonId) {
        char character = '0';
        switch (buttonId) {
            case R.id.digit_0:
                character = '0';
                break;
            case R.id.digit_1:
                character = '1';
                break;
            case R.id.digit_2:
                character = '2';
                break;
            case R.id.digit_3:
                character = '3';
                break;
            case R.id.digit_4:
                character = '4';
                break;
            case R.id.digit_5:
                character = '5';
                break;
            case R.id.digit_6:
                character = '6';
                break;
            case R.id.digit_7:
                character = '7';
                break;
            case R.id.digit_8:
                character = '8';
                break;
            case R.id.digit_9:
                character = '9';
                break;
            case R.id.button_dot:
                character = '.';
                break;
            case R.id.button_divide:
                character = '/';
                break;
            case R.id.button_multiply:
                character = '*';
                break;
            case R.id.button_subtract:
                character = '-';
                break;
            case R.id.button_add:
                character = '+';
                break;
        }
        if (validateCharacter(character)) {
            value += character;
        }
    }

    private boolean validateCharacter(char character) {
        if (Character.isDigit(character)) {
            return true;
        }
        switch (character) {
            case '.':
                if (value.isEmpty()) {
                    return false;
                }
                int index = value.length() - 1;
                while (index >= 0 && Character.isDigit(value.charAt(index))) {
                    index -= 1;
                }
                return index < 0 || value.charAt(index) != '.';
            case '-':
            case '+':
                if (value.isEmpty()) {
                    return true;
                }
            case '*':
            case '/':
                return !value.isEmpty() && Character.isDigit(value.charAt(value.length() - 1));
            default:
                return false;
        }
    }

    private void calculateExpression() {
        if (!value.isEmpty()) {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMinimumFractionDigits(0);
            decimalFormat.setMaximumFractionDigits(DECIMAL_PRECISION);
            decimalFormat.setGroupingUsed(false);
            value = decimalFormat.format(new ExpressionEvaluator(value).evaluate());
        }
    }

    public void handleClick(int buttonId) {
        if (notificationManager != null && notificationBuilder != null) {
            switch (buttonId) {
                case R.id.button_clear:
                    value = "";
                    break;
                case R.id.button_delete:
                    if (value.length() > 0) {
                        value = value.substring(0, value.length() - 1);
                    }
                    break;
                case R.id.button_equal:
                    calculateExpression();
                    break;
                case R.id.button_copy:
                    ClipData clip = ClipData.newPlainText(null, value);
                    clipboardManager.setPrimaryClip(clip);
                    break;
                case R.id.button_paste:
                    if (clipboardManager.getPrimaryClip() != null) {
                        ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
                        if (item != null) {
                            CharSequence data = item.getText();
                            if (data != null) {
                                for (int i = 0; i < data.length(); i++) {
                                    char character = data.charAt(i);
                                    if (validateCharacter(character)) {
                                        value += character;
                                    }
                                }
                            }
                        }
                    }
                    break;
                default:
                    addCharacter(buttonId);
                    break;
            }
            remoteViewsSmall.setTextViewText(R.id.view_display, value);
            remoteViewsLarge.setTextViewText(R.id.view_display, value);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static class ButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainApplication application = (MainApplication) context.getApplicationContext();
            application.handleClick(intent.getIntExtra(EXTRA_ID, 0));
        }

    }

    public static class DismissReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MainApplication application = (MainApplication) context.getApplicationContext();
            application.hideNotification();
        }

    }

    public static class BackgroundService extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }

}
