package com.blackbirds.androidbluetoothprint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.converter.ArabicConverter;
import com.mazenrashed.printooth.data.converter.Converter;
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

public class MainActivity extends AppCompatActivity /*implements PrintingCallback*/ {

//    AppCompatButton btnPrint, btnPrintImage, btnPairUnpair;
//    Printing printing;

    private Printing printing = null;
    PrintingCallback printingCallback=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initView();

        Log.d("xxx", "onCreate ");
        if (Printooth.INSTANCE.hasPairedPrinter())
            printing = Printooth.INSTANCE.printer();
        initViews();
        initListeners();
    }

    private void initViews() {
        if (Printooth.INSTANCE.getPairedPrinter()!=null)
            ((Button) findViewById(R.id.btnPiarUnpair)).setText(
                    (Printooth.INSTANCE.hasPairedPrinter())?("Un-pair "+ Printooth.INSTANCE.getPairedPrinter().getName()):"Pair with printer");
    }



    public void btnPrint(View v) {
        if (!Printooth.INSTANCE.hasPairedPrinter())
            startActivityForResult(new Intent(this, ScanningActivity.class ),ScanningActivity.SCANNING_FOR_PRINTER);
        else printSomePrintable();
    }


    public void btnPrintImages(View v) {
        if (!Printooth.INSTANCE.hasPairedPrinter())
            startActivityForResult(new Intent(this, ScanningActivity.class ),ScanningActivity.SCANNING_FOR_PRINTER);
        else printSomeImages();
    }


    public void btnPiarUnpair(View v) {
        if (Printooth.INSTANCE.hasPairedPrinter()) {Printooth.INSTANCE.removeCurrentPrinter();
        } else {
            startActivityForResult(new Intent(this, ScanningActivity.class ),ScanningActivity.SCANNING_FOR_PRINTER);
            initViews();
        }
    }

    public void btnCustomPrinter(View v) {
        //startActivity(Intent(this, WoosimActivity::class.java))
    }

    private void initListeners() {
        if (printing!=null && printingCallback==null) {
            Log.d("xxx", "initListeners ");
            printingCallback = new PrintingCallback() {

                @Override
                public void disconnected() {

                }

                public void connectingWithPrinter() {
                    Toast.makeText(getApplicationContext(), "Connecting with printer", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "Connecting");
                }
                public void printingOrderSentSuccessfully() {
                    Toast.makeText(getApplicationContext(), "printingOrderSentSuccessfully", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "printingOrderSentSuccessfully");
                }
                public void connectionFailed(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "connectionFailed :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "connectionFailed : "+error);
                }
                public void onError(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "onError :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onError : "+error);
                }
                public void onMessage(@NonNull String message) {
                    Toast.makeText(getApplicationContext(), "onMessage :" +message, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onMessage : "+message);
                }
            };

            Printooth.INSTANCE.printer().setPrintingCallback(printingCallback);
        }
    }

    private void printSomePrintable() {
        Log.d("xxx", "printSomePrintable ");
        if (printing!=null)
            printing.print(getSomePrintables());
    }



    private void printSomeImages() {
        if (printing != null) {
            Log.d("xxx", "printSomeImages ");
            ArrayList<Printable> al = new ArrayList<>();
            Resources resources = getResources();
            //getContext();
            Bitmap image = BitmapFactory.decodeResource(resources,R.drawable.vehicle);
            al.add(new ImagePrintable.Builder(image).build());
            //al.add(new ImagePrintable.Builder(R.drawable.image1,  resources ).build());
            //al.add(ImagePrintable.Builder(R.drawable.image2, resources).build());
            //al.add(ImagePrintable.Builder(R.drawable.image3, resources).build());
            printing.print(al);
        }
    }


    private ArrayList<Printable> getSomePrintables() {
        ArrayList<Printable> al = new ArrayList<>();
        al.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build()); // feed lines example in raw mode

        al.add( (new TextPrintable.Builder())
                .setText("মঙ্গলবার বাংলাদেশে আঘাত হানতে পারে")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("Hello World : été è à €")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("Hello World")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_RIGHT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("اختبار العربية")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_ARABIC_CP864())
                .setNewLinesAfter(1)
                .setCustomConverter(new ArabicConverter()) // change only the converter for this one
                .build());

        return al;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("xxx", "onActivityResult "+requestCode);

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            initListeners();
            printSomePrintable();
        }
        initViews();
    }

//    private void initView() {
//        btnPrint = findViewById(R.id.btnPrint);
//        btnPrintImage = findViewById(R.id.btnPrintImage);
//        btnPairUnpair = findViewById(R.id.btnPairUnpair);
//
//        if (printing != null) {
//            printing.setPrintingCallback(this);
//        }
//
//        /** Event **/
//        btnPairUnpair.setOnClickListener(view -> {
//            if (Printooth.INSTANCE.hasPairedPrinter()) {
//                Printooth.INSTANCE.removeCurrentPrinter();
//            } else {
//                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
//                changePairAndUnpair();
//            }
//        });
//
//        btnPrintImage.setOnClickListener(view -> {
//            if (!Printooth.INSTANCE.hasPairedPrinter()) {
//                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
//            } else {
//                printImages();
//            }
//        });
//
//        btnPrint.setOnClickListener(view -> {
//            if (!Printooth.INSTANCE.hasPairedPrinter()) {
//                startActivityForResult(new Intent(this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
//            } else {
//                printText();
//            }
//        });
//
//        changePairAndUnpair();
//    }
//
//    private void printText() {
//        ArrayList<Printable> printables = new ArrayList<>();
//        printables.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build());
//
//        //Add Text
//        printables.add((new TextPrintable.Builder())
//                .setText("Hello World: Bangladesh Germany Dhaka Cumilla")
//                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
//                .setNewLinesAfter(1)
//                .build());
//
//        //Custom Text
////        printables.add(new TextPrintable.Builder()
////                .setText("Hello World")
////                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
////                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
////                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
////                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
////                .setNewLinesAfter(1)
////                .build());
//
//        printing.print(printables);
//    }
//
//    private void printImages() {
//        ArrayList<Printable> printable = new ArrayList<>();
//
//        // Load Image From Internet
//        Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/d/d7/Android_robot.svg")
//                .into(new Target() {
//                    @Override
//                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        printable.add(new ImagePrintable.Builder(bitmap).build());
//
//                        printing.print(printable);
//                    }
//
//                    @Override
//                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                    }
//
//                    @Override
//                    public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                    }
//                });
//    }
//
//    private void changePairAndUnpair() {
//        if (Printooth.INSTANCE.hasPairedPrinter()) {
//            btnPairUnpair.setText(new StringBuilder("Unpair ")
//                    .append(Printooth.INSTANCE.getPairedPrinter()
//                            .getName()).toString());
//        } else {
//            btnPairUnpair.setText("Pair with printer");
//        }
//    }
//
//    @Override
//    public void connectingWithPrinter() {
//        Toast.makeText(this, "Connection to printer", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void connectionFailed(@NonNull String s) {
//        Toast.makeText(this, "Failed: " + s, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void disconnected() {
//
//    }
//
//    @Override
//    public void onError(@NonNull String s) {
//        Toast.makeText(this, "Error: " + s, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onMessage(@NonNull String s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void printingOrderSentSuccessfully() {
//        Toast.makeText(this, "Order sent to printer", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK){
//            initPrinting();
//        }
//        changePairAndUnpair();
//    }
//
//    private void initPrinting() {
//        if (!Printooth.INSTANCE.hasPairedPrinter()) {
//            printing = Printooth.INSTANCE.printer();
//        }
//        if (printing != null){
//            printing.setPrintingCallback(this);
//        }
//    }
}