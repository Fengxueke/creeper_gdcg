package com.isflee;

import org.apache.commons.lang3.builder.ToStringStyle;

public class DelimiterToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_DELIMITER = "|";

    private String delimiter;

    public DelimiterToStringStyle(){
        this(DEFAULT_DELIMITER);
    }

    public DelimiterToStringStyle(String delimiter){
        super();
        this.delimiter = delimiter;
        this.setNullText("");
        this.setFieldSeparator(this.delimiter);
        this.setUseClassName(false);
        this.setUseIdentityHashCode(false);
        this.setUseFieldNames(false);
        this.setContentStart("");
        this.setContentEnd("");

    }

}
