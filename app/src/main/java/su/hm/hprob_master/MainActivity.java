package su.hm.hprob_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import su.hm.hprob.HorizontalProgressBar;

public class MainActivity extends AppCompatActivity {

    private Button setBtn;
    private EditText valueET;
    private HorizontalProgressBar hpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBtn = (Button) findViewById(R.id.setBtn);
        valueET = (EditText) findViewById(R.id.valueET);
        hpb = (HorizontalProgressBar) findViewById(R.id.hpb);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float value = Float.valueOf(valueET.getText().toString());
                hpb.setPercent(value);
            }
        });
    }
}
