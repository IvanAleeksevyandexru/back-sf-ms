package ru.gosuslugi.pgu.sp.adapter.service;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;

@RunWith(value = Parameterized.class)
public class AddPdf_104_buying_and_selling_Test extends AddPdfServiceBatchTest {

    public AddPdf_104_buying_and_selling_Test(String roleId, Long oid, String directory, TestPdfMode testPdfMode) throws IOException {
        super(roleId, oid, directory, testPdfMode);
    }

    @Override
    protected String getScenarioFilename() {
        return "registration_ts.json";
    }

    @Override
    protected String getPdfPrefix() {
//        return "buying_and_selling";
        return "pdf_add_buying_and_selling";
    }

    @Parameters
    public static Object[][] sumTestData() {
        return new Object[][]{
//                {"Applicant", -10000L, "pdf_add/10000000123/buying_and_selling/buying_and_selling-763519339", TestPdfMode.CHECK},
                {"Applicant", -10000L, "pdf_add/10000000123/buying_and_selling/buying_and_selling-763712780", TestPdfMode.GENERATE},
        };
    }
}
