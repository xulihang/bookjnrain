# -*- coding: utf-8 -*-  
import sqlite3
import xlwt

#查询
#def find():

def main():
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute('select price from book')
    res = c.fetchall()
    wbk = xlwt.Workbook(encoding='utf-8')
    sheet = wbk.add_sheet('sheet 1')
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,1,row)
        
    c.execute('select title from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,0,row)

    c.execute('select publisher from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,2,row) 

    c.execute('select code from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,3,row)

    c.execute('select pubdate from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,4,row)

    c.execute('select lasttime from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,5,row)
    sheet.write(0,0,"书名")
    sheet.write(0,1,"价格")
    sheet.write(0,2,"出版社")
    sheet.write(0,3,"ISBN号")
    sheet.write(0,4,"出版日期")
    sheet.write(0,5,"上次扫描时间")
    wbk.save('test.xls') 

main()
