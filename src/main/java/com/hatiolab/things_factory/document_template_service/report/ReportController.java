package com.hatiolab.things_factory.document_template_service.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.type.HtmlSizeUnitEnum;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/report")
public class ReportController {
	@PostMapping(path = "/show_pdf", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> showPdf(@RequestParam String template, @RequestParam String jsonString)
			throws Exception {
		if (template.isEmpty()) {
			return new ResponseEntity<>("please select a file!", HttpStatus.OK);
		}

		try {

			ByteArrayInputStream is = new ByteArrayInputStream(template.getBytes());
			// 2. JasperReport 정보를 로딩
			JasperReport jasperReport = this.loadReportByJRxml(is);

			InputStream jsonInputStream = new ByteArrayInputStream(jsonString.getBytes());
			JsonDataSource jds = new JsonDataSource(jsonInputStream);

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// 4. JapserReport와 데이터소스로 리포트를 response의 output stream에 write
			this.writeReportToStream("pdf", jasperReport, null, jds, os);

			return new ResponseEntity<>(os.toByteArray(), HttpStatus.OK);

		} catch (JRException jre) {
			throw jre;

		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping(path = "/show_html", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> showHtml(@RequestParam String template, @RequestParam String jsonString) throws Exception {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(template.getBytes());
			// 2. JasperReport 정보를 로딩
			JasperReport jasperReport = this.loadReportByJRxml(is);
			InputStream jsonInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
			JsonDataSource jds = new JsonDataSource(jsonInputStream);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// 4. JapserReport와 데이터소스로 리포트를 response의 output stream에 write
			this.writeReportToStream("html", jasperReport, null, jds, os);

			return new ResponseEntity<>(os.toByteArray(), HttpStatus.OK);

		} catch (JRException jre) {
			throw jre;

		} catch (Exception e) {
			throw e;
		}
	}

	@PostMapping(path = "/show_html", produces = MediaType.APPLICATION_JSON_VALUE, params = "template, jsonString, isFile")
	public ResponseEntity<?> showHtml(@RequestParam MultipartFile template, @RequestParam String jsonString,
			@RequestParam Boolean isFile) throws Exception {

		if (template.isEmpty()) {
			return new ResponseEntity<>("please select a file!", HttpStatus.OK);
		}

		try {
			ByteArrayInputStream is = new ByteArrayInputStream(template.getBytes());
			// 2. JasperReport 정보를 로딩
			JasperReport jasperReport = this.loadReportByJRxml(is);
			InputStream jsonInputStream = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));
			JsonDataSource jds = new JsonDataSource(jsonInputStream);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// 4. JapserReport와 데이터소스로 리포트를 response의 output stream에 write
			this.writeReportToStream("html", jasperReport, null, jds, os);

			return new ResponseEntity<>(os.toByteArray(), HttpStatus.OK);

		} catch (JRException jre) {
			throw jre;

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Jasper JRXML 내용으로 부터 리포트 로딩
	 * 
	 * @param jrxmlContent @return @throws
	 */
	private JasperReport loadReportByJRxml(ByteArrayInputStream is) throws Exception {
		JasperDesign jasperDesign = JRXmlLoader.load(is);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		return jasperReport;
	}

	private void writeReportToStream(String type, JasperReport jasperReport, Map<String, Object> parameters,
			JRDataSource jds, OutputStream os) throws Exception {
		switch (type) {
		case "html":
			writeHtmlReportToStream(jasperReport, parameters, jds, os);
			break;
		case "pdf":
		default:
			writePdfReportToStream(jasperReport, parameters, jds, os);
			break;
		}
	}

	/**
	 * 리포트 데이터를 스트림에 write
	 * 
	 * @param jasperReport @param parameter @param os @throws
	 */
	private void writePdfReportToStream(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource jds,
			OutputStream os) throws Exception {
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jds);

		if (jasperPrint != null) {
			JasperExportManager.exportReportToPdfStream(jasperPrint, os);
		}

	}

	/**
	 * 리포트 데이터를 스트림에 write
	 * 
	 * @param jasperReport @param parameter @param os @throws
	 */
	private void writeHtmlReportToStream(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource jds,
			OutputStream os) throws Exception {

		SimpleHtmlReportConfiguration configuration = new SimpleHtmlReportConfiguration();
//		configuration.setIgnorePageMargins(true);
		configuration.setSizeUnit(HtmlSizeUnitEnum.POINT);

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jds);
		HtmlExporter exporter = new HtmlExporter();

		exporter.setConfiguration(configuration);

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(os));

		exporter.exportReport();
	}

}
