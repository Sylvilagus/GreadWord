package com.sylva.greadword

import groovy.transform.Canonical

/**
 * Created by sylva on 2016/3/15.
 */
class WSerialParts {
    static final String[] cnEles = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"]
    static final List<List<Serial>> serials = new ArrayList<>()
    static {
        def s1 = new ArrayList()
        def s2 = new ArrayList()
        def s3 = new ArrayList()
        def s4 = new ArrayList()
        def s5 = new ArrayList()
        (1..99).each {
            s1.add(new Serial(type: 4, text: it + "."))
            s2.add(new Serial(type: 3, text: it + "、"))
            s3.add(new Serial(type: 2, text: "（${translateArabToCn(it)}）"))
            s4.add(new Serial(type: 1, text: "（${translateArabToCn(it)}）、"))
            s5.add(new Serial(type: 0, text: "${translateArabToCn(it)}、"))
        }
        serials.add(s1)
        serials.add(s2)
        serials.add(s3)
        serials.add(s4)
        serials.add(s5)

    }

    static Serial matchSerial(String line) {
        Serial serial
        serials.each {
            it.find {
                if (line?.trim()?.startsWith(it.text))
                    serial=it
            }
        }
        serial
    }

    static translateArabToCn(int arab) {
        def aStr = arab.toString()
        if (aStr.length() == 1)
            cnEles[arab]
        else if (aStr.length() == 2) {
            int first = aStr[0].toInteger()
            int second = aStr[1].toInteger()
            (first == 1 ? "" : cnEles[first]) + "十" + cnEles[second]
        }else{
            null
        }
    }
    @Canonical
    static class Serial {
        long id
        int type
        String text
    }
}
