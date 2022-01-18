package ru.gosuslugi.pgu.sp.adapter.service;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;

@RunWith(value = Parameterized.class)
public class AddPdf_venicle_gov_list_Test extends AddPdfServiceBatchTest {

    public AddPdf_venicle_gov_list_Test(String roleId, Long oid, String directory, TestPdfMode testPdfMode) throws IOException {
        super(roleId, oid, directory, testPdfMode);
    }

    @Override
    protected String getScenarioFilename() {
        return "registration_ts.json";
    }

    @Override
    protected String getPdfPrefix() {
        return "pdf_add_venicle_gov_list";
    }

    @Parameters
    public static Object[][] sumTestData() {
        return new Object[][]{
                {"Applicant", -10000L, "pdf_add/10000000123/venicle_gov_list/venicle_gov_list-first", TestPdfMode.GENERATE},
                {"Applicant", -10000L, "pdf_add/10000000123/venicle_gov_list/venicle_gov_list-second", TestPdfMode.GENERATE},
        };
    }
}
