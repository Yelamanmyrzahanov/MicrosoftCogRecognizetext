package kz.djunglestones.microsoftcogrecognizetext;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LabelActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        textView = (TextView)findViewById(R.id.store_text);
        SharedPreferences prefs = getSharedPreferences("stringbuilder", MODE_PRIVATE);
        String stringBuilder = prefs.getString("outcome",null);
        textView.setText(stringBuilder);
        String[] values = stringBuilder.split("\n");
        for (int i=0;i<values.length;i++){
            Log.i("value"+i, "onCreate: "+values[i]);
        }
    }
}
