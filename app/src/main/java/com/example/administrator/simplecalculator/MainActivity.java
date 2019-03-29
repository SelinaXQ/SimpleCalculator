package com.example.administrator.simplecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private Button[] btn = new Button[10];
    private EditText input;
    private Button div, mul, sub, add, equal,sqrt,dot,c;
    public boolean inputLock = true;// true means no no error of input
    public boolean pressEql = true;//whether press equal, if pressed, you can not input. true means your input is before "=".
    public String oldStr, newStr;
    public boolean first = true;//whether the first input, if not append into input

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = (EditText)findViewById(R.id.input);
        btn[0] = (Button)findViewById(R.id.zero);
        btn[1] = (Button)findViewById(R.id.one);
        btn[2] = (Button)findViewById(R.id.two);
        btn[3] = (Button)findViewById(R.id.three);
        btn[4] = (Button)findViewById(R.id.four);
        btn[5] = (Button)findViewById(R.id.five);
        btn[6] = (Button)findViewById(R.id.six);
        btn[7] = (Button)findViewById(R.id.seven);
        btn[8] = (Button)findViewById(R.id.eight);
        btn[9] = (Button)findViewById(R.id.nine);
        div = (Button)findViewById(R.id.divide);
        mul = (Button)findViewById(R.id.mul);
        sub = (Button)findViewById(R.id.sub);
        add = (Button)findViewById(R.id.add);
        equal = (Button)findViewById(R.id.equal);
        sqrt = (Button)findViewById(R.id.sqrt);
        dot = (Button)findViewById(R.id.dot);
        c = (Button)findViewById(R.id.c);
        for(int i = 0; i < 10; ++i) {
            btn[i].setOnClickListener(actionPerformed);
        }
        div.setOnClickListener(actionPerformed);
        mul.setOnClickListener(actionPerformed);
        sub.setOnClickListener(actionPerformed);
        add.setOnClickListener(actionPerformed);
        equal.setOnClickListener(actionPerformed);
        sqrt.setOnClickListener(actionPerformed);
        dot.setOnClickListener(actionPerformed);
        c.setOnClickListener(actionPerformed);

    }

    private View.OnClickListener actionPerformed = new View.OnClickListener() {
        public void onClick(View v) {
            String command = ((Button)v).getText().toString(); //Button text
            String str = input.getText().toString();
            if(!pressEql){
                input.setText("Press C before new input.");
            }
            if("0123456789.+-×÷√".indexOf(command) != -1 && inputLock) {
                if(first)//clear input
                    input.setText(command);
                else
                    input.append(command);
                first = false;
            }  else if(command.compareTo("C") == 0) {
                input.setText("0");//delete all
                first = inputLock = pressEql = true;
            } else if(command.compareTo("=") == 0 && inputLock  && pressEql) {//press "=" can not input anymore unless you press C
                oldStr = str;
                inputLock = pressEql = false;
                first = true;
                newStr = str.replaceAll("-", "-1×");
                new calculate().process(newStr);
            }
            inputLock = true;
        }
    };
    public class calculate {
        public calculate(){ }
        final int MAXLEN = 500;

        public void process(String str) {//scan from left to right.
            int opCount = 0; //number of operator in weight/operator
            int numCount = 0;//number of number in number[]
            int flag = 1;//-1 is negative
            int weightTemp = 0;//current operator weight
            int weight[];
            double number[];
            char ch, chCurrent;
            char operator[];
            String num;
            weight = new int[MAXLEN];
            number = new double[MAXLEN];
            operator = new char[MAXLEN];
            String expression = str;
            StringTokenizer expToken = new StringTokenizer(expression,"+-×÷√");
            int i = 0;
            while (i < expression.length()) {
                ch = expression.charAt(i);
                if (i == 0) {
                    if (ch == '-')
                        flag = -1;
                }
                if (ch <= '9' && ch >= '0'|| ch == '.') {//get number
                    num = expToken.nextToken();
                    chCurrent = ch;
                    Log.e("qxiang",ch+"ch index:"+i);
                    while (i < expression.length() &&
                            (chCurrent <= '9' && chCurrent >= '0'|| chCurrent == '.')) {
                        chCurrent = expression.charAt(i++);
                        Log.e("qxiang","current chCurrent is:"+chCurrent +" i is："+i);
                    }
                    if (i >= expression.length()) i-=1; else {i-=2;}//if reach the last char i = i-1; else i = i - 2
                    if (num.compareTo(".") == 0) number[numCount++] = 0;//the expression is only a dot. the result would be 0.0
                    else {
                        number[numCount++] = Double.parseDouble(num)*flag;
                        flag = 1;
                    }
                }
                if (ch == '-' && flag == 1 || ch == '+' || ch == '×'|| ch == '÷' || ch == '√' ) {
                    switch (ch) {
                        case '+':
                        case '-':
                            weightTemp = 1;
                            break;
                        case '×':
                        case '÷':
                            weightTemp = 2;
                            break;
                        //case '√':
                        default:
                            weightTemp = 3;
                            break;
                    }
                    //if current weight higher than the first operator, than add it into the operator
                    if (opCount == 0 || weight[opCount -1] < weightTemp) {
                        weight[opCount] = weightTemp;
                        operator[opCount] = ch;
                        opCount++;

                    }else {//compute all the operator, until current operator's weight higher than the operater in operator[]
                        while (opCount > 0 && weight[opCount -1] >= weightTemp) {
                            switch (operator[opCount -1]) {
                                case '+':
                                    number[numCount-2]+=number[numCount-1];
                                    break;
                                case '-':
                                    number[numCount-2]-=number[numCount-1];
                                    break;
                                case '×':
                                    number[numCount-2]*=number[numCount-1];
                                    break;
                                case '÷':
                                    if (number[numCount-1] == 0) {
                                        input.setText("\""+oldStr+"\""+": "+"0 can't be a divisor.");
                                        return;
                                    }
                                    number[numCount-2]/=number[numCount-1];
                                    break;
                                case '√':
                                    if(number[numCount-1] == 0 || (number[numCount-2] < 0 && number[numCount-1] % 2 == 0)) {
                                        input.setText("\""+oldStr+"\""+": "+"invalid input.");
                                        return;
                                    }
                                    number[numCount-2] =
                                            Math.pow(number[numCount-2], 1/number[numCount-1]);
                                    break;
                            }
                            numCount--;
                            opCount--;
                        }
                        weight[opCount] = weightTemp;
                        operator[opCount] = ch;
                        opCount++;
                    }
                }
                i++;
            }
            while (opCount >0) {//compute in order
                switch (operator[opCount -1]) {
                    case '+':
                        number[numCount-2]+=number[numCount-1];
                        break;
                    case '-':
                        number[numCount-2]-=number[numCount-1];
                        break;
                    case '×':
                        number[numCount-2]*=number[numCount-1];
                        break;
                    case '÷':
                        if (number[numCount-1] == 0) {
                            input.setText("\""+oldStr+"\""+": "+"0 can't be a divisor.");
                            return;
                        }
                        number[numCount-2]/=number[numCount-1];
                        break;
                    case '√':
                        if( ( number[numCount-2] < 0 && number[numCount-1] % 2 == 0 )||  number[numCount-1] == 0 ) {
                            input.setText("\""+oldStr+"\""+": "+"invalid input.");
                            return;
                        }
                        number[numCount-2] =
                                Math.pow(number[numCount-2], 1/number[numCount-1]);
                        break;
                }
                numCount--;
                opCount--;
            }
            if(number[0] > 7.3E306) {//number out of range
                input.setText("\""+oldStr+"\""+": "+"out of range");
                return;
            }
            DecimalFormat format = new DecimalFormat("0.############");
            input.setText(String.valueOf(Double.parseDouble(format.format(number[0]))));
        }
    }
}
