# -*- coding: utf-8 -*-  
from bottle import route, run, template, request
import sqlite3
import json
import urllib


#登录
@route("/login",method="post")
def login():
    global isbn
    username = request.forms.get("username");
    password = request.forms.get("password");
    isbn = request.forms.get("isbn");
    print(username,password,isbn)
    if username =='admin' and password =='admin':
        insert()
        return username+'登录成功';
    else :
        return username+'登录失败';

#用户登录页面的
@route("/index")
def index():
    
    return template("index")

def insert():
    params = isbn
    f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+str(params))
    jresult=f.read()
    jsonVal = json.loads(jresult)
    bookcode=jsonVal["isbn13"]
    bookname=jsonVal["title"]
    bookprice=jsonVal["price"]
    bookpublisher=jsonVal["publisher"]
    bookpubdate=jsonVal["pubdate"]
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("insert into book values ('"+bookname+"','"+bookprice+"','"+bookpublisher+"','"+bookcode+"','"+bookpubdate+"')")
    conn.commit()
    c.close()
    conn.close()
    
#@route('/hello/:name')
#def index(name='World'):
#    return '<b>Hello %s!</b>' % name


#默认端口  run(host='localhost', port=8080)
run(host='192.168.1.104', port=80)
