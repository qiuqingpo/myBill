package com.qiuqp.mybill.common;

public class CalExpression {
	 // 整体运算
    public Double calculate(String total) {
        int position1 = 0;
        int position2 = 0;
 
        // 分析表达式是否有括号
        Boolean had = false;
 
        for (int i = 0; i < total.length(); i++) {
            if (total.charAt(i) == '(')
                had = true;
        }
 
        // 查一下为什么indexof在此不能用~~~~谁知道的话告诉一声，下列代码错在哪里~~~
 
        // 用错这里 查了一小时菜查出错误
 
        if (had == true)// 只需处理第一个括号，然后递归调用自身，有括号继续处理，没括号就得果
 
        {
            for (int i = 0; i < total.length(); i++) {
                if (total.charAt(i) == '(') {
                    position1 = i;
                    continue;
                }
                if (total.charAt(i) == ')') {
                    position2 = i;
                    break;
                }
            }
            // 这句重点，有点长
 
            return calculate(total.substring(0, position1).trim()
                    + calculate(total.substring(position1 + 1, position2)
                            .trim())
                    + total.substring(position2 + 1, total.length()).trim());
 
        } else
            return returnInner(total);
    }
 
    // 针对括号内的运算,即未带括号的表达式~~~有的话递归调用自身
    private Double returnInner(String expression) {
        Double result = 0.0;
        int op1 = 0;
        int op2 = 0;
        char operator = ' ';
        int count = 0;
 
        // 此循环用于获得前两个操作符的位置
        for (int i = 0; i < expression.length(); i++) {
            operator = expression.charAt(i);
            if (operator == 42 || operator == 43 || operator == 45
                    || operator == 47) {
                if (count == 0) {
                    op1 = i;
                    count++;
                    continue;
                }
                if (count == 1) {
                    op2 = i;
                    break;
                }
            }
        }
 
        // 判断第一个操作符，并用递归计算整个表达式~~~~~
 
        // 即把未带括号的表达式拆成第一个数与其后面的表达式 进行运算
        // 这整个算法很经典~~~~呵呵~~有参考网上的解析~~~觉得这个最短最好用
        // 当然，括号太多~~仔细辨清各个整体~~~
        if(count==0)
        {
        	result = Double.parseDouble(expression);
        	return result;
        }
        operator = expression.charAt(op1);
        if (operator == '+') {
            result = Double.parseDouble(expression.substring(0, op1).trim());// 获取第一个数
            if (op2 > 0)
                result += returnInner(expression.substring((op1 + 1), expression.length()));// 有两个以上操作符~~~递归
            else
                result += Double.parseDouble(expression.substring(op1 + 1,
                        expression.length()).trim());// 只有一个+号
        } else if (operator == '-') // 同+运算
        {
            result = Double.parseDouble(expression.substring(0, op1).trim());
            if (op2 > 0)
                result -= returnInner(expression.substring((op1 + 1),
                        expression.length()));
            else
                result -= Double.parseDouble(expression.substring(op1 + 1,
                        expression.length()).trim());
        } else if (operator == '*') {
            if (op2 > 0)// 有两个以上操作符，先第一个数*第二个数 然后在与后面的字符组成一个表达式，带入递归 有用空格特意局开整体
                result = returnInner((Double.parseDouble(expression.substring(
                        0, op1).trim()) * Double.parseDouble(expression
                        .substring((op1 + 1), op2)))
                        + expression.substring(op2, expression.length()));
            else
                result = (Double.parseDouble(expression.substring(0, op1)
                        .trim()) * Double.parseDouble(expression.substring(
                        (op1 + 1), expression.length())));
            return result;
        } else if (operator == '/') // 同*运算
        {
            if (op2 > 0)
                result = returnInner((Double.parseDouble(expression.substring(
                        0, op1).trim()) / Double.parseDouble(expression
                        .substring((op1 + 1), op2)))
                        + expression.substring(op2, expression.length()));
            else
                result = (Double.parseDouble(expression.substring(0, op1)
                        .trim()) / Double.parseDouble(expression.substring(
                        (op1 + 1), expression.length())));
        }
 
        return result;
    }
}
