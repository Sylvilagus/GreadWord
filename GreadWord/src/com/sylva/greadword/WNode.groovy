package com.sylva.greadword

import groovy.transform.Canonical

/**
 * Created by sylva on 2016/3/15.
 */
@Canonical
class WNode {
    long id
    long pid
    String content
    WSerialParts.Serial serial
}
