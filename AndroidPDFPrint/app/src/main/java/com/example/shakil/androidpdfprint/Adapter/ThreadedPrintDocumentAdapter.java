package com.example.shakil.androidpdfprint.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadedPrintDocumentAdapter extends PrintDocumentAdapter {
    abstract LayoutJob buildLayoutJob(PrintAttributes oldAttributes,
                                      PrintAttributes newAttributes,
                                      CancellationSignal cancellationSignal,
                                      LayoutResultCallback callback,
                                      Bundle extras);

    abstract WriteJob buildWriteJob(PageRange[] pages,
                                    ParcelFileDescriptor destination,
                                    CancellationSignal cancellationSignal,
                                    WriteResultCallback callback,
                                    Context ctxt);

    private Context context = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

    ThreadedPrintDocumentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {
        threadPool.submit(buildLayoutJob(oldAttributes, newAttributes,
                cancellationSignal, callback,
                extras));
    }

    @Override
    public void onWrite(PageRange[] pages,
                        ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        threadPool.submit(buildWriteJob(pages, destination,
                cancellationSignal, callback, context));
    }

    @Override
    public void onFinish() {
        threadPool.shutdown();

        super.onFinish();
    }

    protected abstract static class LayoutJob implements Runnable {
        PrintAttributes oldAttributes;
        PrintAttributes newAttributes;
        CancellationSignal cancellationSignal;
        LayoutResultCallback callback;
        Bundle extras;

        LayoutJob(PrintAttributes oldAttributes,
                  PrintAttributes newAttributes,
                  CancellationSignal cancellationSignal,
                  LayoutResultCallback callback, Bundle extras) {
            this.oldAttributes = oldAttributes;
            this.newAttributes = newAttributes;
            this.cancellationSignal = cancellationSignal;
            this.callback = callback;
            this.extras = extras;
        }
    }

    protected abstract static class WriteJob implements Runnable {
        PageRange[] pages;
        ParcelFileDescriptor destination;
        CancellationSignal cancellationSignal;
        WriteResultCallback callback;
        Context context;

        WriteJob(PageRange[] pages, ParcelFileDescriptor destination,
                 CancellationSignal cancellationSignal,
                 WriteResultCallback callback, Context context) {
            this.pages = pages;
            this.destination = destination;
            this.cancellationSignal = cancellationSignal;
            this.callback = callback;
            this.context = context;
        }
    }
}