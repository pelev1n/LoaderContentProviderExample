package com.codexpedia.loader;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private TextView tvJsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvJsonResult = (TextView) findViewById(R.id.tv_json_result);
        getSupportLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks<String>)this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new FetchData(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        tvJsonResult.setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    private static class FetchData extends AsyncTaskLoader<String> {

        public FetchData(Context context) {
            super(context);
        }

        @Override
        public String loadInBackground() {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;
            String line;
            try {
                URL url = new URL("https://itunes.apple.com/search?term=classic");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) buffer.append(line);

                if (buffer.length() == 0) return null;
                jsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("MainActivity", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivity", "Error closing stream", e);
                    }
                }
            }

            return PrettyJson.prettify(jsonStr);
        }

        @Override
        public void deliverResult(String data) {
            super.deliverResult(data);
        }
    }

}
