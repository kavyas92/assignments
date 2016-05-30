package models.utilities;

import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import controllers.SerializableManager;
import models.Account;
import models.Bank;
import models.Person;

public class AdminReports {
	private static Bank bank = new Bank();
	private static SerializableManager m = new SerializableManager();
	private static String FILE = "C:/Users/Bolo/tema4/Tema4/AdminReports.pdf";
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static double sum = 0;

	public AdminReports() {
		try {
			bank = m.deserializeBank();
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(FILE));
			document.open();
			addTitlePage(document);
			addContent(document, writer);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static JFreeChart generateBarChart(Bank b) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		Iterator<Entry<Person, ArrayList<Account>>> iterator = b.getContent().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Person, ArrayList<Account>> entry = iterator.next();
			for (int i = 0; i < entry.getValue().size(); i++) {
				try {
					dataSet.setValue(
							df.parse(entry.getValue().get(i).getCloseDate()).getYear()
									- df.parse(entry.getValue().get(i).getDate()).getYear(),
							"Year", String.valueOf(entry.getValue().get(i).getId()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		JFreeChart chart = ChartFactory.createBarChart("Account valability", "AccountId", "Year", dataSet,
				PlotOrientation.VERTICAL, false, true, false);

		return chart;
	}

	public static void writeChartToPDF(Document document, JFreeChart chart, int width, int height, PdfWriter writer) {

		try {
			PdfContentByte contentByte = writer.getDirectContent();
			PdfTemplate template = contentByte.createTemplate(width, height);
			@SuppressWarnings("deprecation")
			Graphics2D graphics2d = template.createGraphics(width, height, new DefaultFontMapper());
			java.awt.geom.Rectangle2D rectangle2d = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);

			chart.draw(graphics2d, rectangle2d);

			graphics2d.dispose();
			contentByte.addTemplate(template, 0, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Report generated!");
	}

	public static void addTitlePage(Document document) throws DocumentException {
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("Generated report for admin", catFont));
		addEmptyLine(preface, 1);
		preface.add(new Paragraph("Report generated by: admin, " + new Date(), smallBold));
		addEmptyLine(preface, 3);
		preface.add(new Paragraph("This document describes the operations made on each account", smallBold));
		addEmptyLine(preface, 8);
		document.add(preface);
		document.newPage();
	}

	public static void addContent(Document document, PdfWriter writer) throws DocumentException {
		Anchor anchor = new Anchor("Deposits", catFont);
		anchor.setName("Deposits");
		Random rand = new Random();
		Paragraph para = new Paragraph(anchor);
		addEmptyLine(para, 1);
		createTable(para);
		addEmptyLine(para, 1);
		createPersonTable(para);
		addEmptyLine(para, 1);
		para.add(new Paragraph("The amount of money from deposits: " + sum, smallBold));
		addEmptyLine(para, 1);
		para.add(new Paragraph("The total number of accounts is: " + bank.getNumberOfAccounts(), smallBold));
		addEmptyLine(para, 1);
		para.add(new Paragraph("The total number of persons is: " + bank.getNumberOfPersons(), smallBold));
		addEmptyLine(para, 1);
		para.add(new Paragraph("Interest rate: 0.1. Next increase will be done in " + (rand.nextInt(10) + 1)
				+ " months with " + (rand.nextInt(5) + 1) + "%", smallBold));
		addEmptyLine(para, 1);
		para.add(new Paragraph("Gain rate: 0.1. Next increase will be done in " + (rand.nextInt(10) + 1)
				+ " months with " + (rand.nextInt(2) + 1) + "%", smallBold));
		addEmptyLine(para, 2);
		writeChartToPDF(document, generateBarChart(bank), 400, 300, writer);
		document.add(para);

	}

	public static void createTable(Paragraph subCatPart) throws BadElementException {
		PdfPTable table = new PdfPTable(7);

		PdfPCell c1 = new PdfPCell(new Phrase("ID"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Person"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("AccountID"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Sum"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Type"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Open Date"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase("Closing Date"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);

		Iterator<Entry<Person, ArrayList<Account>>> iterator = bank.getContent().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Person, ArrayList<Account>> entry = iterator.next();
			for (int i = 0; i < entry.getValue().size(); i++) {
				sum += entry.getValue().get(i).getSum();

				PdfPCell cell1 = new PdfPCell(new Paragraph(String.valueOf(entry.getKey().getId())));
				PdfPCell cell2 = new PdfPCell(new Paragraph(entry.getKey().getName()));
				PdfPCell cell3 = new PdfPCell(new Paragraph(String.valueOf(entry.getValue().get(i).getId())));
				PdfPCell cell4 = new PdfPCell(new Paragraph(String.valueOf(entry.getValue().get(i).getSum())));
				PdfPCell cell5 = new PdfPCell(new Paragraph(entry.getValue().get(i).getType()));
				PdfPCell cell6 = new PdfPCell(new Paragraph(entry.getValue().get(i).getDate()));
				PdfPCell cell7 = new PdfPCell(new Paragraph(entry.getValue().get(i).getCloseDate()));

				table.addCell(cell1);
				table.addCell(cell2);
				table.addCell(cell3);
				table.addCell(cell4);
				table.addCell(cell5);
				table.addCell(cell6);
				table.addCell(cell7);
			}
		}
		subCatPart.add(table);

	}

	public static void createPersonTable(Paragraph subCatPart) throws BadElementException {
		PdfPTable table = new PdfPTable(2);

		PdfPCell c1 = new PdfPCell(new Phrase("ID"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Person"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);

		Iterator<Entry<Person, ArrayList<Account>>> iterator = bank.getContent().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Person, ArrayList<Account>> entry = iterator.next();
			PdfPCell cell1 = new PdfPCell(new Paragraph(String.valueOf(entry.getKey().getId())));
			PdfPCell cell2 = new PdfPCell(new Paragraph(entry.getKey().getName()));

			table.addCell(cell1);
			table.addCell(cell2);

		}
		subCatPart.add(table);

	}

	public static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}