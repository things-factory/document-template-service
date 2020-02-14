//package com.hatiolab.thingsfactory.documenttemplate.xlsx2pdf;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.poi.EncryptedDocumentException;
//import org.apache.poi.ss.usermodel.BorderStyle;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.CellType;
//import org.apache.poi.ss.usermodel.HorizontalAlignment;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.VerticalAlignment;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.jxls.common.Context;
//import org.jxls.util.JxlsHelper;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.TextRenderingMode;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Div;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.element.Text;
//
//@RestController
//public class Xlsx2PdfController {
//	@PostMapping(path = "/xlsx2pdf")
//	public ResponseEntity<?> convertXlsx2Pdf(@RequestParam MultipartFile xlsx, @RequestParam String data) {
//
//		if (xlsx.isEmpty()) {
//			return new ResponseEntity<>("please select a file!", HttpStatus.OK);
//		}
//
//		ByteArrayOutputStream outputStream = processTemplate(Arrays.asList(xlsx), data);
//
//		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//		ByteArrayOutputStream pdfOutputStream = convert(inputStream);
//		return new ResponseEntity<>(pdfOutputStream.toByteArray(), HttpStatus.OK);
//
////		return new ResponseEntity<>(outputStream.toByteArray(), HttpStatus.OK);
//
//	}
//
//	private ByteArrayOutputStream processTemplate(List<MultipartFile> files, String jsonString) {
//		for (MultipartFile file : files) {
//
//			if (file.isEmpty()) {
//				continue; // next pls
//			}
//
//			Gson gson = new Gson();
//			Type stringObjectMapType = new TypeToken<Map<String, Object>>() {
//			}.getType();
//			Map<String, Object> data = gson.fromJson(jsonString, stringObjectMapType);
//
//			Context context = new Context();
//
//			for (Map.Entry<String, Object> entry : data.entrySet()) {
//				context.putVar(entry.getKey(), entry.getValue());
//			}
//
//			ByteArrayOutputStream os = new ByteArrayOutputStream();
//
//			try {
//				JxlsHelper.getInstance().processTemplate(file.getInputStream(), os, context);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			return os;
//		}
//		return null;
//	}
//
//	private ByteArrayOutputStream convert(ByteArrayInputStream is) {
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		try {
//			XSSFWorkbook xlsxWorkbook = new XSSFWorkbook(is);
//			XSSFSheet xlsxWorksheet = xlsxWorkbook.getSheetAt(0);
//
//			// To iterate over the rows
//			Iterator<Row> rowIterator = xlsxWorksheet.iterator();
//
//			// We will create output PDF document objects at this point
//			PdfWriter pdfWriter = new PdfWriter(os);
//			PdfDocument pdfDoc = new PdfDocument(pdfWriter);
//			Document document = new Document(pdfDoc);
//
//			// get column count
//			Integer maxColumnCount = 0;
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//				Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
//				Integer columnCount = 0;
//				while (cellIterator.hasNext()) {
//					org.apache.poi.ss.usermodel.Cell cell = cellIterator.next(); // Fetch CELL
//					columnCount++;
//				}
//				if (columnCount > maxColumnCount)
//					maxColumnCount = columnCount;
//			}
//
//			Table pdfTable = new Table(maxColumnCount);
//
//			rowIterator = xlsxWorksheet.iterator();
//
//			List<CellRangeAddress> mergedRegions = xlsxWorksheet.getMergedRegions();
//
//			// Loop through rows.
//			while (rowIterator.hasNext()) {
//				Cell tableCell;
//				Row row = rowIterator.next();
//				Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
//				while (cellIterator.hasNext()) {
//					org.apache.poi.ss.usermodel.Cell cell = cellIterator.next(); // Fetch CELL
//
//					Integer colIdx = cell.getColumnIndex();
//					Integer rowIdx = cell.getRowIndex();
//
//					Boolean noAdd = false;
//
//					tableCell = new Cell();
//
//					for (CellRangeAddress mergedRegion : mergedRegions) {
//						if (mergedRegion.isInRange(rowIdx, colIdx)) {
//							if (mergedRegion.getFirstRow() == rowIdx && mergedRegion.getFirstColumn() == colIdx) {
//								Integer rowSpan = mergedRegion.getLastRow() - mergedRegion.getFirstRow() + 1;
//								Integer colSpan = mergedRegion.getLastColumn() - mergedRegion.getFirstColumn() + 1;
//								tableCell = new Cell(rowSpan, colSpan);
//							} else {
//								noAdd = true;
//							}
//							
//						}
//					}
//
//					switch (cell.getCellType()) { // Identify CELL type
//
//					// you need to add more code here based on your requirement / transformations
//					case STRING: // Push the data from Excel to PDF Cell
//						// feel free to move the code below to suit to your needs
//						Paragraph p = new Paragraph(cell.getStringCellValue());
//						p.setTextRenderingMode(TextRenderingMode.FILL);
//						tableCell.add(p);
//						break;
//					case BOOLEAN: 
////						
////						
////						Cell c = new Cell(cell.getBooleanCellValue());
////						tableCell.add(c);
//						break;
////					case FORMULA: 
////						Text t = new Text(cell.fo);
////						tableCell.add(t);
////						break;
//					case NUMERIC: 
////						Text t = new Text(cell.getNumericCellValue());
////						tableCell.add(t);
//						break;
//					default:
//						break;
//					}
//
//					if (tableCell != null) {
//						float columnWidth = xlsxWorksheet.getColumnWidthInPixels(colIdx);
//						float rowHeight = row.getHeightInPoints();
//						tableCell.setWidth(columnWidth);
//						tableCell.setHeight(rowHeight);
//						
//						tableCell.setTextRenderingMode(TextRenderingMode.FILL);
//						
//						tableCell.setBorder(Border.NO_BORDER);
//
//						CellStyle cellStyle = cell.getCellStyle();
//
//						BorderStyle borderTop = cellStyle.getBorderTop();
//						BorderStyle borderRight = cellStyle.getBorderRight();
//						BorderStyle borderBottom = cellStyle.getBorderBottom();
//						BorderStyle borderLeft = cellStyle.getBorderLeft();
//
//						VerticalAlignment vAlign = cellStyle.getVerticalAlignment();
//						HorizontalAlignment hAlign = cellStyle.getAlignment();
//
//						switch (vAlign) {
//						case TOP:
//							tableCell.setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.TOP);
//							break;
//						case BOTTOM:
//							tableCell.setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.BOTTOM);
//							break;
//						case CENTER:
//							tableCell.setVerticalAlignment(com.itextpdf.layout.property.VerticalAlignment.MIDDLE);
//							break;
//						default:
//							break;
//						}
//
//						switch (hAlign) {
//						case LEFT:
//							tableCell.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.LEFT);
//							break;
//						case CENTER:
//							tableCell.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);
//							break;
//						case RIGHT:
//							tableCell.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.RIGHT);
//							break;
//						default:
//							break;
//						}
//					}
//
//					if (!noAdd) {
//						pdfTable.addCell(tableCell);
//						System.out.println(tableCell.getRow() + ", " + tableCell.getCol()  + ", " +  tableCell.getRowspan()  + ", " +  tableCell.getColspan() + " - " + (cell.getCellType() == CellType.STRING ? cell.getStringCellValue(): ""));
//					}
//				}
//			}
//			document.add(pdfTable);
//			document.close();
//			xlsxWorkbook.close();
////			} // Finally add the table to PDF document 
//			return os;
////				}
////			}
//
//		} catch (EncryptedDocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return os;
//	}
//}
