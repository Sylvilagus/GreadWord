package com.sylva.greadword

import groovy.sql.Sql
import groovy.swing.SwingBuilder
import groovy.swing.impl.TableLayout
import org.apache.poi.hwpf.extractor.WordExtractor

import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.TitledBorder

/**
 * Created by sylva on 2016/3/15.
 */
class WordImporter {
    static DB_URL = "jdbc:mysql://localhost:3306/knowlegedepot12333"
    static DB_USERNAME = "root"
    static DB_PASSWORD = "password"
    static DEFAULT_FILE_PATH = "D:\\资料汇编印刷\\"
    static JTextField mTfUrl, mTfUserName, mTfPassword, filePath
    static JLabel mLbErr, mLbBottomHint
    static Sql sql
    static TableLayout mImportPan
    static JTextArea mTaLog
    static JButton mBtnLink, mBtnImport

    static Sql link() {
        Sql.newInstance(mTfUrl.text, mTfUserName.text,
                mTfPassword.text, "com.mysql.jdbc.Driver")
    }

    static log(String msg) {
        mTaLog.text += "\r\n" + msg
        mTaLog.setCaretPosition(mTaLog.text.length())
    }

    static thread(Closure clo) {
        new Thread() {
            @Override
            void run() {
                clo()
            }
        }.start()
    }

    static main(args) {
        def swingBuilder = new SwingBuilder()
        swingBuilder.frame(title: "导入word文件到数据库", defaultCloseOperation: JFrame.EXIT_ON_CLOSE, size: [600, 400], show: true) {
            panel {
                tableLayout(border: new TitledBorder("配置")) {
                    tr {
                        td {
                            label(text: "数据库链接")
                        }
                        td {
                            mTfUrl = textField(columns: 40, text: DB_URL)
                        }
                    }
                    tr {
                        td {
                            label(text: "用户名")
                        }
                        td {
                            mTfUserName = textField(columns: 40, text: DB_USERNAME)
                        }

                    }
                    tr {
                        td {
                            label(text: "密码")
                        }
                        td {
                            mTfPassword = textField(columns: 40, text: DB_PASSWORD)
                        }

                    }
                    tr {
                        td {
                            mBtnLink = button(text: "连接", actionPerformed: {
                                mLbErr.text = "正在连接，请稍候"
                                mBtnLink.visible = false
                                thread {
                                    try {
                                        sql = link()
                                        mLbErr.text = "数据库连接成功"
                                        mImportPan.visible = true
                                        mBtnLink.visible = false
                                    } catch (Exception e) {
                                        e.printStackTrace()
                                        mLbErr.text = "数据库连接失败"
                                        mBtnLink.visible = true
                                        mImportPan.visible = false
                                    }
                                }
                            })
                        }
                        td {
                            mLbErr = label()
                        }
                    }
                }
                mImportPan = tableLayout(border: new TitledBorder("导入文件")) {
                    tr {
                        td {
                            label("导入文件夹")
                        }
                        td {
                            filePath = textField(columns: 40, text: DEFAULT_FILE_PATH)
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            mBtnImport = button(text: "导入", actionPerformed: {
                                mBtnImport.visible = false
                                log "开始导入"
                                mLbBottomHint.visible = true
                                mLbBottomHint.text = "正在导入，导入未完成请不要关闭窗口，否则会导致数据不完整"
                                thread {
                                    try {
                                        def fileId = UUID.randomUUID().toString()
                                        def fileCount=0;
                                        new File(filePath.text).eachFileRecurse { file ->
                                            if (file.name.endsWith(".doc")) {
                                                try {
                                                    def fis = new FileInputStream(file)
                                                    def paras
//                                                try {
                                                    def document = new WordExtractor(fis)
                                                    paras = document.getText()
//                                                }catch (NotOLE2FileException e){
//                                                    def document = new XWPFWordExtractor(new XWPFDocument(fis))
//                                                    paras = document.getText()
//                                                }
                                                    def nodes = new WParagraphSpliter().splitWord(paras)
                                                    nodes.each {
                                                        def insertStmt = "insert into T_NODE(node_id,parent_node_id,document_id,document_name,content) values (${it.id},${it.pid},${fileId},$file.name,${it.content})"
                                                        sql.executeInsert(insertStmt)
                                                    }
                                                    fis.close()
                                                    log "文件 ${file.path} 导入成功!共导入 ${nodes.size()} 条记录"
                                                } catch (Exception e) {
                                                    e.printStackTrace()
                                                    log file.path + "导入失败， 格式不支持。"
                                                }
                                                fileId= UUID.randomUUID().toString()
                                                fileCount++
                                            }
                                        }
                                        log "导入完毕，共成功导入 ${fileCount} 个文件"
                                        mLbBottomHint.visible = false
                                    }catch(Exception e){
                                        log "导入失败"
                                        mLbBottomHint.visible = false
                                        mBtnImport.visible = true
                                    }
                                }
                            })
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            scrollPane() {
                                mTaLog = textArea(columns: 45, rows: 8, autoscrolls: true)
                            }
                        }
                    }
                    tr {
                        td(colspan: 2) {
                            mLbBottomHint = label()
                        }
                    }
                }
                mImportPan.visible = false
            }
        }
    }
}
