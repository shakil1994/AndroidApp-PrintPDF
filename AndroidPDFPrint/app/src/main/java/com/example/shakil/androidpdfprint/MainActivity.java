package com.example.shakil.androidpdfprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.example.shakil.androidpdfprint.Adapter.PdfDocumentAdapter;
import com.example.shakil.androidpdfprint.Service.PrintJobMonitorService;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_ID = 1337;
    private WebView wv = null;
    private PrintManager mgr = null;
    private EditText prose = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prose = findViewById(R.id.prose);
        mgr = (PrintManager) getSystemService(PRINT_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bitmap:
                Intent i =
                        new Intent(Intent.ACTION_GET_CONTENT)
                                .addCategory(Intent.CATEGORY_OPENABLE)
                                .setType("image/*");

                startActivityForResult(i, IMAGE_REQUEST_ID);

                return (true);

            case R.id.web:
                printWebPage();

                return (true);

            case R.id.report:
                printReport();

                return (true);

            case R.id.pdf:
                print("Test PDF",
                        new PdfDocumentAdapter(getApplicationContext()),
                        new PrintAttributes.Builder().build());

                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_ID
                && resultCode == Activity.RESULT_OK) {
            try {
                PrintHelper help = new PrintHelper(this);

                help.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                help.printBitmap("Photo!", data.getData());
            } catch (FileNotFoundException e) {
                Log.e(getClass().getSimpleName(), "Exception printing bitmap",
                        e);
            }
        }
    }

    private void printWebPage() {
        WebView print = prepPrintWebView(getString(R.string.web_page));

        print.loadUrl("https://commonsware.com/Android");
    }

    private void printReport() {
        Template tmpl =
                Mustache.compiler().compile(getString(R.string.report_body));
        WebView print = prepPrintWebView(getString(R.string.tps_report));

        print.loadData(tmpl.execute(new TpsReportContext(prose.getText().toString())), "text/html; charset=UTF-8", null);
    }

    private WebView prepPrintWebView(final String name) {
        WebView result = getWebView();

        result.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                print(name, view.createPrintDocumentAdapter(),
                        new PrintAttributes.Builder().build());
            }
        });

        return (result);
    }

    private WebView getWebView() {
        if (wv == null) {
            wv = new WebView(this);
        }

        return (wv);
    }

    private PrintJob print(String name, PrintDocumentAdapter adapter,
                           PrintAttributes attrs) {
        startService(new Intent(this, PrintJobMonitorService.class));

        return (mgr.print(name, adapter, attrs));
    }

    private static class TpsReportContext {
        private static final SimpleDateFormat fmt =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String msg;

        TpsReportContext(String msg) {
            this.msg = msg;
        }

        @SuppressWarnings("unused")
        String getReportDate() {
            return (fmt.format(new Date()));
        }

        @SuppressWarnings("unused")
        String getMessage() {
            return (msg);
        }
    }
}