package ru.gosuslugi.pgu.sp.adapter.data;

import lombok.val;
import org.junit.Test;
import ru.atc.carcass.security.rest.model.orgs.OrgType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TemplatesDataContextTest {
    private final TemplatesDataContext context = new TemplatesDataContext();

    @Test
    public void nullTestOrgTYpe() {
        val orgType = context.getOrgType();
        assertNull(orgType);
    }

    @Test
    public void AGENCYTestOrgTYpe() {
        context.getAdditionalValues().put("orgType", "AGENCY");
        val orgType = context.getOrgType();
        assertEquals(orgType, OrgType.AGENCY);
    }

    @Test
    public void LEGALTestOrgTYpe() {
        context.getAdditionalValues().put("orgType", "LEGAL");
        val orgType = context.getOrgType();
        assertEquals(orgType, OrgType.LEGAL);
    }

    @Test
    public void BUSINESSTestOrgTYpe() {
        context.getAdditionalValues().put("orgType", "BUSINESS");
        val orgType = context.getOrgType();
        assertEquals(orgType, OrgType.BUSINESS);
    }
}
