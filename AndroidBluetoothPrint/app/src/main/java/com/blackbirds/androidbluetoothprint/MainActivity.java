package com.blackbirds.androidbluetoothprint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PrintingCallback {

    AppCompatButton btnPrint, btnPrintImage, btnPairUnpair;
    Printing printing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btnPrint = findViewById(R.id.btnPrint);
        btnPrintImage = findViewById(R.id.btnPrintImage);
        btnPairUnpair = findViewById(R.id.btnPairUnpair);

        if (printing != null) {
            printing.setPrintingCallback(this);
        }

        /** Event **/
        btnPairUnpair.setOnClickListener(view -> {
            if (Printooth.INSTANCE.hasPairedPrinter()) {
                Printooth.INSTANCE.removeCurrentPrinter();
            } else {
                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                changePairAndUnpair();
            }
        });

        btnPrintImage.setOnClickListener(view -> {
            if (!Printooth.INSTANCE.hasPairedPrinter()) {
                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
            } else {
                printImages();
            }
        });

        btnPrint.setOnClickListener(view -> {
            if (!Printooth.INSTANCE.hasPairedPrinter()) {
                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
            } else {
                printText();
            }
        });

        changePairAndUnpair();
    }

    private void printText() {
        ArrayList<Printable> printable = new ArrayList<>();
        printable.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build());

        //Add Text
        printable.add(new TextPrintable.Builder()
                .setText("Hello World: Bangladesh Germany Dhaka Cumilla")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        //Custom Text
        printable.add(new TextPrintable.Builder()
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        printing.print(printable);
    }

    private void printImages() {
        ArrayList<Printable> printable = new ArrayList<>();

        // Load Image From Internet
        Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        printable.add(new ImagePrintable.Builder(bitmap).build());

                        printing.print(printable);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void changePairAndUnpair() {
        if (Printooth.INSTANCE.hasPairedPrinter()) {
            btnPairUnpair.setText(new StringBuilder("Unpair ")
                    .append(Printooth.INSTANCE.getPairedPrinter()
                            .getName()).toString());
        } else {
            btnPairUnpair.setText("Pair with printer");
        }
    }

    @Override
    public void connectingWithPrinter() {
        Toast.makeText(this, "Connection to printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(@NonNull String s) {
        Toast.makeText(this, "Failed: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disconnected() {

    }

    @Override
    public void onError(@NonNull String s) {
        Toast.makeText(this, "Error: " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(@NonNull String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        Toast.makeText(this, "Order sent to printer", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK){
            initPrinting();
        }
        changePairAndUnpair();
    }

    private void initPrinting() {
        if (!Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
        }
        if (printing != null){
            printing.setPrintingCallback(this);
        }
    }
}