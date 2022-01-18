package ru.gosuslugi.pgu.sp.adapter.service;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;

@RunWith(value = Parameterized.class)
public class Pdf_10000000101_zagranpassport_Test extends PdfServiceBatchTest {


    public Pdf_10000000101_zagranpassport_Test(String roleId, Long oid, String directory, TestPdfMode testPdfMode) throws IOException {
        super(roleId, oid, directory, testPdfMode);
    }

    @Override
    protected String getScenarioFilename() {
        return "zagranpassport.json";
    }

    @Override
    protected String getPdfPrefix() {
        return "pdf";
    }

    @Parameters
    public static Object[][] sumTestData() {
        return new Object[][]{
            {"Applicant", -10000L, "pdf/10000000101_zagranpassport/pdf-zagranpassport-763500855-EPGUCORE-41134", TestPdfMode.GENERATE},
        };
    }
}
