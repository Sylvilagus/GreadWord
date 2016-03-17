package com.sylva.greadword

/**
 * Created by sylva on 2016/3/15.
 */
class WParagraphSpliter {

    long nodeId
    WNode wNodePart
    Stack<WSerialParts.Serial> pSerialStack =new Stack<>()
    WSerialParts.Serial formerSerial
    List<WNode> splitWord(String doc){
        List<WNode> wNodes=new LinkedList<>()
        def index=0
        doc.eachLine {
            if(index==0) {
//                wNodes.add(new WNode(id: ++nodeId, pid: 0, content: it))
                def id=++nodeId
                pSerialStack.push(new WSerialParts.Serial(id:id,type:-1,text: "root"))
                formerSerial=new WSerialParts.Serial(id:id,type:-1)
                wNodePart=new WNode(id:id,pid:pSerialStack.peek().id,content: it,serial:formerSerial )
            }else{
                WSerialParts.Serial serial=WSerialParts.matchSerial(it)
                if(serial!=null){
                    def id=++nodeId
                    serial.id=id
                    //same
                    if(serial.type<formerSerial?.type){
                        WSerialParts.Serial directParent=pSerialStack.peek()
                        while (serial.type<directParent.type){
                            directParent=pSerialStack.pop()
                            println "pop a element from stack"
                        }
                        wNodes.add(wNodePart)

                    }else if(serial.type>formerSerial?.type){
                        wNodes.add(wNodePart)
                        pSerialStack.push(wNodePart.serial)
                    }else
                        wNodes.add(wNodePart)
                    wNodePart=new WNode(id:id,pid:pSerialStack.peek().id,content: it,serial:serial)
                    formerSerial=serial
                }else{
                    wNodePart.content+=it
                }
            }
            index++
        }
        wNodes.add(wNodePart)
        wNodes
    }

}
