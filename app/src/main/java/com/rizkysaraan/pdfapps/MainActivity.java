package com.rizkysaraan.pdfapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnCreatePdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreatePdf = findViewById(R.id.btn_create_pdf);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btnCreatePdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createPDFFile(Common.getAppPath(MainActivity.this)+"test.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void createPDFFile(String path) {
        if(new File(path).exists())
            new File(path).delete();


        try {
            Document document = new Document();
            //save
            PdfWriter.getInstance(document, new FileOutputStream(path));

            //open to write
            document.open();

            //setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("PT. Japati Indonesia");
            document.addCreator("Rizky Saraan");

            //font setting
            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            //Custom Font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon-medium.otf","UTF-8",BaseFont.EMBEDDED);


            //create title of document
            Font titleFont = new Font(fontName,36.0f,Font.NORMAL,BaseColor.BLACK);
            addNewItem(document,"Order Details", Element.ALIGN_CENTER,titleFont);


            //add more
            Font orderNumberFont = new Font(fontName,fontSize, Font.NORMAL,colorAccent);
            addNewItem(document,"Order No:",Element.ALIGN_LEFT,orderNumberFont);

            Font orderNumberValueFont = new Font(fontName,valueFontSize, Font.NORMAL,BaseColor.BLACK);
            addNewItem(document,"#717171",Element.ALIGN_LEFT,orderNumberValueFont);


            addLineSeparator(document);

            addNewItem(document,"Order Date", Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document, "3/03/2020",Element.ALIGN_LEFT,orderNumberValueFont);

            addLineSeparator(document);

            addNewItem(document,"Account Name:", Element.ALIGN_LEFT,orderNumberFont);
            addNewItem(document, "Rizy Saraan",Element.ALIGN_LEFT,orderNumberValueFont);

            addLineSeparator(document);


            //add product order detail
            addLineSeparator(document);
            addNewItem(document,"Product Detail",Element.ALIGN_CENTER,titleFont);
            addLineSeparator(document);

            //item 1
            addNewItemWithLeftAndRight(document, "Pizza 25","(0.0%)",titleFont,orderNumberValueFont);
            addNewItemWithLeftAndRight(document,"12.0*1000","12.000",titleFont,orderNumberValueFont);

            addLineSeparator(document);

            addNewItemWithLeftAndRight(document, "Pizza 26","(0.0%)",titleFont,orderNumberValueFont);
            addNewItemWithLeftAndRight(document,"12.0*1000","12.000",titleFont,orderNumberValueFont);

            addLineSeparator(document);

            //Total
            addLineSpace(document);
            addLineSpace(document);


            addNewItemWithLeftAndRight(document, "Total","24000.0",titleFont,orderNumberValueFont);

            document.close();

            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();


            printPDF();

        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (DocumentException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private void printPDF() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(MainActivity.this,Common.getAppPath(MainActivity.this)+"test.pdf");
            printManager.print("Document",printDocumentAdapter, new PrintAttributes.Builder().build());

        }catch (Exception ex){
            Log.e("Japati",""+ex.getMessage());
        }
    }

    private void addNewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft,textLeftFont);
        Chunk chunkTextRight = new Chunk(textRight,textRightFont);

        Paragraph p =new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);


    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,69));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));

    }


    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text,font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
    }


}
