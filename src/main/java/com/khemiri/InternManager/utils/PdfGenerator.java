package com.khemiri.InternManager.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.khemiri.InternManager.entities.Stage;
import com.khemiri.InternManager.entities.Stagiaire;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class PdfGenerator {

        public ByteArrayInputStream generateInternshipCertificate(Stagiaire stagiaire, Stage stage) {
        final String entreprise = "Smart IT Partner";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            // Add logo
            InputStream logoStream =getClass().getResourceAsStream("/sip_logo.jpg");
            assert logoStream != null;
            ImageData logoData = ImageDataFactory.create(logoStream.readAllBytes());
            Image logo = new Image(logoData);
            float logoWidth = 200f;
            float logoHeight = 200f;
            float marginTop = 1.5f;
            logo.setWidth(UnitValue.createPointValue(logoWidth));
            logo.setHeight(UnitValue.createPointValue(logoHeight));
            logo.setFixedPosition(1, 36f,  pdfDoc.getDefaultPageSize().getTop() - marginTop - logoHeight);
            document.add(logo);


            // Titre
            Paragraph titre = new Paragraph("Attestation de Stage");
            titre.setBold();
            titre.setUnderline();
            titre.setFontSize(32f);
            titre.setMarginBottom(50f);
            titre.setMarginTop(180f);
            titre.setTextAlignment(TextAlignment.CENTER);
            document.add(titre);

            Paragraph content = new Paragraph("Je soussigné(e), représentant de " + entreprise.toUpperCase() +
                    ", atteste que "+ stagiaire.getNom().toUpperCase() + " " + stagiaire.getPrenom().toUpperCase() +
                    " a effectué un stage au sein de notre entreprise du "
                    +dateFormat.format(stage.getDateDebut())+ " au " + dateFormat.format(stage.getDateFin())
                    +" sous la supervision de " + stage.getTuteur().toUpperCase()+
                    " dans le cadre de sa formation.");

            content.setMarginBottom(40f);
            content.setFontSize(22f);
            document.add(content);

            // Signature et date
            String date = dateFormat.format(new Date());
            String formattedDate = "Ariana le : " + date;
            String signature = "Signature de l'encadrant";

            Paragraph signatureParagraph = new Paragraph(signature);
            signatureParagraph.setMarginTop(170f);
            signatureParagraph.setFontSize(20f);
            document.add(signatureParagraph);

            Paragraph dateParagraph = new Paragraph(formattedDate);
            dateParagraph.setMarginBottom(20f);
            dateParagraph.setFontSize(20f);
            document.add(dateParagraph);


            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}

