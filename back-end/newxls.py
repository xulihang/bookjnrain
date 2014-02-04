# -*- coding: utf-8 -*-  
import sqlite3
import xlwt

#查询
#def find():

def main():
    borders = xlwt.Borders() # Create Borders
    borders.left = xlwt.Borders.MEDIUM # May be: NO_LINE, THIN, MEDIUM, DASHED, DOTTED, THICK, DOUBLE, HAIR, MEDIUM_DASHED, THIN_DASH_DOTTED, MEDIUM_DASH_DOTTED,     THIN_DASH_DOT_DOTTED, MEDIUM_DASH_DOT_DOTTED, SLANTED_MEDIUM_DASH_DOTTED, or 0x00 through 0x0D.
    borders.right = xlwt.Borders.MEDIUM
    borders.top = xlwt.Borders.MEDIUM
    borders.bottom = xlwt.Borders.MEDIUM
    borders.left_colour = 0x40
    borders.right_colour = 0x40
    borders.top_colour = 0x40
    borders.bottom_colour = 0x40
    style = xlwt.XFStyle() # Create Style
    style.borders = borders # Add Borders to Style

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
        sheet.write(i,1,row,style)
        
    c.execute('select title from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,0,row,style)

    c.execute('select publisher from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,2,row,style) 

    c.execute('select code from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,3,row,style)

    c.execute('select pubdate from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,4,row,style)

    c.execute('select lasttime from book')
    res = c.fetchall()
    i=0
    for row in res:
        i=i+1
        # indexing is zero based, row then column
        sheet.write(i,5,row,style)
    sheet.write(0,0,"书名",style)
    sheet.write(0,1,"价格",style)
    sheet.write(0,2,"出版社",style)
    sheet.write(0,3,"ISBN号",style)
    sheet.write(0,4,"出版日期",style)
    sheet.write(0,5,"上次扫描时间",style)
    wbk.save('test.xls') 

main()
