package com.example.fansh.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private TextView screen;
    private String num1="";
    private String num2="";
    private String op="";
    private BigDecimal memory = BigDecimal.ZERO;
    private boolean isResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = (TextView) findViewById(R.id.screen);
        screen.setText("");
    }

    //-----------input-------------
    public void onClickNumber(View v){
        Button button = (Button) v;
        String num = button.getText().toString();
        if (checkIsClear()|| checkIsNum()){
            // num
            if(checkIsResult() || num1.equals("0")|| num1.isEmpty()) num1 = num;
            else if(checkDigits(num1)) num1 = num1+ num;
            screen.setText(num1);
        }else{
            //numop numopnum
            if(checkIsResult() || num2.equals("0")|| num2.isEmpty()) num2 = num;
            else if(checkDigits(num2)) num2 = num2+ num;
            screen.setText(num2);
        }
        isResult = false;
    }

    public void onClickPoint(View v){
        if(checkIsClear() || checkIsNum()){
            // num
            if(checkIsResult()|| num1.isEmpty()) num1 = "0.";
            else if(!num1.contains(".")) num1 = num1+ ".";
            screen.setText(num1);
        }else {
            // numop numopnum
            if(checkIsResult()|| num2.isEmpty()) num2 = "0.";
            else if(!num2.contains(".")) num2 = num2+ ".";
            screen.setText(num2);
        }
        isResult = false;
    }

    public void onClickMRC(View v) {
        String mem = memory.toString();
        if(checkIsClear() || checkIsNum()){
            //error num
            num1 = mem;
            screen.setText(num1);
        }else {
            // numop numopnum
            num2 = mem;
            screen.setText(num2);
        }
        isResult = true;
        memory = BigDecimal.ZERO;
    }

    public void onClickMemoryAdd(View v) {
        String screenText = screen.getText().toString();
        if(!screenText.isEmpty() && !screenText.equals("error")){
            memory = memory.add(new BigDecimal(screen.getText().toString()));
            memory = new BigDecimal(getResult(memory));
            isResult = true;
        }
    }

    public void onClickMemoryMinus(View v) {
        String screenText = screen.getText().toString();
        if(!screenText.isEmpty() && !screenText.equals("error")){
            memory = memory.subtract(new BigDecimal(screen.getText().toString()));
            memory = new BigDecimal(getResult(memory));
            isResult = true;
        }
    }

    //-----------calculate-------------
    public void onClickOperator(View v){
        Button button = (Button) v;
        if(checkIsClear()){
            // do nothing
        }else if(checkIsNumOpNum()){
            // numopnum
            String result = operate(num1, num2, op).toString();
            clear();
            if(result.equals("error")){
                screen.setText("error");
            }else{
                num1 = result;
                op = button.getText().toString();
                screen.setText(result);
            }
        }else{
            // num // numop only change op, not error
            op = button.getText().toString();
        }
        isResult = false;
        return;
    }

    public void onClickEqual(View v){
        if(checkIsClear()){
            // do nothing;
        }else if(checkIsNumOpNum()) {
            // numopnum
            String result = operate(num1, num2, op).toString();
            clear();
            if(result.equals("error")){
                screen.setText("error");
            }else{
                num1 = result;
                screen.setText(result);
            }
        }else{
            // num //numop
            clear();
        }
        isResult = true;
        return;
    }

    public void onClickPercent(View v){
        if(checkIsClear()){
            // do nothing;
        }else if(checkIsNumOpNum()) {
            // numopnum
            String result = percent(num1, num2, op).toString();
            clear();
            if(result.equals("error")){
                screen.setText("error");
            }else{
                num1 = result;
                screen.setText(result);
            }
        }else{
            // num //numop
            String result = percent(num1, "", "").toString();
            clear();
            num1 = result;
            screen.setText(result);
        }
        isResult = true;
        return;
    }


    public void onClickSqrt(View v){
        if(checkIsClear()){
            //do nothing
        }else{
            String screenText = screen.getText().toString();
            Double temp = new Double(screenText);
            temp = Math.sqrt(temp);
            clear();
            if(temp.isNaN()){
                screen.setText("error");
            }else{
                BigDecimal result = new BigDecimal(temp.toString());
                num1 = getResult(result);
                screen.setText(num1);
            }
        }
        isResult = true;
        return;
    }

    public void onClickClear(View v){
        clear();
        screen.setText("");
    }

    private void clear(){
        num1 = "";
        num2 = "";
        op = "";
    }

    private String operate(String num1,String num2,String op){
        BigDecimal result = new BigDecimal(num1);;
        if("+".equals(op)){
            result = result.add(new BigDecimal(num2));
        }else if("-".equals(op)){
            result = result.subtract(new BigDecimal(num2));
        }else if("×".equals(op)){
            result = result.multiply(new BigDecimal(num2));
        }else if("÷".equals(op)){
            if(num2.equals("0")){
                return "error";
            }else{
                result = result.divide(new BigDecimal(num2),10, RoundingMode.HALF_UP);
            }
        }
        return getResult(result);
    }

    private String percent(String num1,String num2,String op){
        BigDecimal n1 = new BigDecimal(num1);
        if(num2.isEmpty()){
            //Num Numop
            n1 = n1.divide(new BigDecimal("100"));
            return getResult(n1);
        }else{
            BigDecimal n2 = new BigDecimal(num2);
            n2 = n2.divide(new BigDecimal("100"));
            if("+".equals(op)){
                n2 = new BigDecimal("1").add(n2);
            }else if("-".equals(op)){
                n2 = new BigDecimal("1").subtract(n2);
            }else if("×".equals(op)){
                // do nothing
            }else if("÷".equals(op)){
                if(num2.equals("0")){
                    return "error";
                }else{
                    n2 = new BigDecimal("1").divide(n2,10, RoundingMode.HALF_UP);
                }
            }
            n2 = n1.multiply(n2);
            return getResult(n2);
        }
    }

    private String getResult(BigDecimal param) {
        int length_n = param.precision();
        int length_d = param.scale();

        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setGroupingUsed(false);// 000,000,
        df.setRoundingMode(RoundingMode.HALF_UP);

        if (length_n > 8 || length_d > 7) {
            Double param_d = new Double(param.toString());
            String param_str = param_d.toString();

            if (param_str.contains("E")) {
                // scientific notation integer digit= 1
                df.setMaximumFractionDigits(7);
                String param_str_i = param_str.substring(0, param_str.indexOf("E"));
                param_str = df.format(new BigDecimal(param_str_i)) + param_str.substring(param_str.indexOf("E"));
            } else {
                // not scientific notation integer digit differ from size 0.and  1.
                if (param.abs().compareTo(BigDecimal.ONE) == 1) {
                    df.setMaximumFractionDigits(8 - (length_n - length_d));
                } else {
                    df.setMaximumFractionDigits(7);
                }
                param_str = df.format(new BigDecimal(param_str));
            }
            return param_str;
        } else {
            df.setMaximumFractionDigits(7);
            return df.format(param);
        }
    }

    private boolean checkDigits(String param){
        BigDecimal num = new BigDecimal(param);
        int length_n = num.precision();
        int length_d = num.scale();
        if(length_n<8 && length_d<7){
            return true;
        }
        return false;
    }

    private boolean checkIsNum(){
        if(!num1.isEmpty() && op.isEmpty() && num2.isEmpty()){
            return true;
        }
        return false;
    }

    private boolean checkIsNumOpNum(){
        if(!num1.isEmpty() && !op.isEmpty() && !num2.isEmpty()){
            return true;
        }
        return false;
    }

    private boolean checkIsClear(){
        if(num1.isEmpty() && op.isEmpty() && num2.isEmpty()){
            return true;
        }
        return false;
    }

    private boolean checkIsResult(){
        if(isResult){
            return true;
        }
        return false;
    }
}
