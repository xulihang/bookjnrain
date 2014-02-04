# -*- coding: utf-8 -*- 
import sqlite3
from pyh import *

def genhtml():
    page = PyH('My wonderful PyH page')
    table1 = page << table()
    tr0 = table1 << tr()
    tr0 << td('书名') + td('价格') + td('出版社') + td('ISBN号') + td('出版日期') + td('上次扫描时间') 
    page.addCSS('/static/page.css')
    page << head('<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=utf-8">')
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    for ss in c.execute('select * from book'):
        title=ss[0].encode('utf8')
        price=ss[1].encode('utf8')
        publisher=ss[2].encode('utf8')
        isbn=ss[3].encode('utf8')
        pubdate=ss[4].encode('utf8')
        lasttime=ss[5].encode('utf8')
        tr1 = table1 << tr()
        tr1 << td(title) + td(price) + td(publisher) + td(isbn) + td(pubdate) + td(lasttime) 
    page.printOut('./static/out.html')

genhtml()
