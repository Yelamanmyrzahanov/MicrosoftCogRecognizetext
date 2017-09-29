package kz.djunglestones.microsoftcogrecognizetext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private VisionServiceClient visionServiceClient = new VisionServiceRestClient("b17b49d1bc5346cdbe1e092ba6fc400a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.udos);
        ImageView imageView = (ImageView)findViewById(R.id.image_view);
        imageView.setImageBitmap(bitmap);

        Button buttonProcess = (Button)findViewById(R.id.processBtn);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        buttonProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream,String,String> recognizeTextText = new AsyncTask<InputStream, String, String>() {
                    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                    @Override
                    protected String doInBackground(InputStream... params)  {
                        try {
                            publishProgress("Recognizing....");
                            OCR ocr = visionServiceClient.recognizeText(params[0], LanguageCodes.Russian,true);
                            String result = new Gson().toJson(ocr);
                            return result;
                        }catch (Exception ex){
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();
                        OCR ocr = new Gson().fromJson(s,OCR.class);
                        TextView textView1 = (TextView)findViewById(R.id.text1);
                        StringBuilder stringBuilder = new StringBuilder();

                        for (Region region:ocr.regions){
                            for (Line line:region.lines){
                                for (Word word:line.words){
                                    stringBuilder.append(word.text+" ");
                                }stringBuilder.append("\n");
                            }
                            stringBuilder.append("\n\n");
                        }
                        textView1.setText(stringBuilder.toString());
                        SharedPreferences.Editor editor = getSharedPreferences("stringbuilder",MODE_PRIVATE).edit();
                        editor.putString("outcome", stringBuilder.toString());
                        editor.apply();
                        final Intent intent = new Intent(MainActivity.this, LabelActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };
                recognizeTextText.execute(inputStream);
            }
        });
    }
}
