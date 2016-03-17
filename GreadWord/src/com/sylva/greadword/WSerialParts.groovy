package com.sylva.greadword

import groovy.transform.Canonical

/**
 * Created by sylva on 2016/3/15.
 */
class WSerialParts {
    static final String[] cnEles = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"]
    static final List<List<Serial>> serials = new ArrayList<>()
    static {
        def arabWithDot = new ArrayList()
        def arabWithDun = new ArrayList()
        def cn = new ArrayList()
        (1..99).each {
            arabWithDot.add(new Serial(type: 4, text: it + "."))
            arabWithDun.add(new Serial(type: 3, text: it + "、"))
            cn.add(new Serial(type: 2, text: "(${translateArabToCn(it)})"))
            cn.add(new Serial(type: 1, text: "(${translateArabToCn(it)})、"))
            cn.add(new Serial(type: 0, text: "${translateArabToCn(it)}、"))
        }
        serials.add(arabWithDot)
        serials.add(arabWithDun)
        serials.add(cn)

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
