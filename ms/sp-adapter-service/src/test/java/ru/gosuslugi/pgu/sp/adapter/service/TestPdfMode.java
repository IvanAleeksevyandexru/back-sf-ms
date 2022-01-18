package ru.gosuslugi.pgu.sp.adapter.service;

public enum TestPdfMode {
    GENERATE(true),
    CHECK(false);

    private final boolean generate;

    TestPdfMode(boolean generate) {
        this.generate = generate;
    }

    public boolean isGenerate() {
        return generate;
    }
}
