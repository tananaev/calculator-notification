package com.tananaev.calculator;

public class ExpressionEvaluator {

    private String expression;
    private int position = -1;
    private int character;

    public ExpressionEvaluator(String expression) {
        this.expression = expression;
    }

    private void nextCharacter() {
        position += 1;
        if (position < expression.length()) {
            character = expression.charAt(position);
        } else {
            character = -1;
        }
    }

    private boolean eat(int charToEat) {
        if (character == charToEat) {
            nextCharacter();
            return true;
        }
        return false;
    }

    public double evaluate() {
        nextCharacter();
        return parseExpression();
    }

    private double parseExpression() {
        double x = parseTerm();
        while (true) {
            if (eat('+')) {
                x += parseTerm();
            } else if (eat('-')) {
                x -= parseTerm();
            } else {
                return x;
            }
        }
    }

    private double parseTerm() {
        double x = parseFactor();
        while (true) {
            if (eat('*')) {
                x *= parseFactor();
            } else if (eat('/')) {
                x /= parseFactor();
            } else {
                return x;
            }
        }
    }

    private double parseFactor() {
        if (eat('+')) {
            return parseFactor();
        }
        if (eat('-')) {
            return -parseFactor();
        }

        double x = 0;

        int startPosition = this.position;
        if (Character.isDigit(character) || character == '.') {
            while (Character.isDigit(character) || character == '.') {
                nextCharacter();
            }
            x = Double.parseDouble(expression.substring(startPosition, this.position));
        }

        return x;
    }

}
