# -*- coding: utf-8 -*-  
import os
from bottle import route, run, template, request, static_file
import sqlite3
import json
import urllib
import time

#登录
@route("/login",method="post")
def login():
    #global isbn
    #global lasttime
    username = request.forms.get("username")
    password = request.forms.get("password")
    isbn = request.forms.get("isbn")
    bookname = request.forms.get("bookname")
    lasttime = request.forms.get("time")
    print(username,password,isbn,lasttime)
    exist=1#默认插入
    print exist
    if auth()=='登录成功!':
        
        #判断是否已经存在
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        for isbncode in c.execute('select code from book'):
            if str("(u'"+isbn+"',)") == str(isbncode):
                exist=0
                conn.commit()
                c.close()
                conn.close()
                break
        print exist
        if exist==0:
            update(isbn,lasttime,bookname,username)
        else:
            insert(isbn,lasttime,bookname,username)
        

        return username+'登录成功'
    else :
        return username+'登录失败'
    
#用户注册
@route("/user/new",method="post")
def newuser():
    username = request.forms.get("username")
    password = request.forms.get("password")
    exist=1#默认注册
    print exist
    #判断是否已经存在
    conn = sqlite3.connect('db/users.db')
    c = conn.cursor()
    
    for dbusername in c.execute('select username from users'):
        print dbusername
        if str("(u'"+username+"',)") == str(dbusername):
            exist=0
            conn.commit()
            c.close()
            conn.close()
            break
            
    print exist
    if exist==0:
        return '该用户名已注册！'
    else:
        c.execute("insert into users values ('"+username+"','"+password+"')")
        conn.commit()
        c.close()
        conn.close()
        conn = sqlite3.connect("db/"+username+".db")
        c = conn.cursor()
        c.execute("create table book (title , price, publisher, isbn, pubdate, lasttime, introduction)")
        conn.commit()
        c.close()
        conn.close()
        return '注册成功！'
    
#用户注册页面
@route("/user/register")
def register():
    return template("register")

#用户登录页面
@route("/user/index")
def register():
    return template("userlogin")

#验证用户
@route("/user/login",method="post")
def auth():
    username = request.forms.get("username")
    password = request.forms.get("password")
    userexist=0 #默认用户不存在
    conn = sqlite3.connect('db/users.db')
    c = conn.cursor()
    for dbusername in c.execute('select username from users'):        
        if str("(u'"+username+"',)") == str(dbusername):
            userexist=1
            c.execute("select password from users where username='"+username+"'")
            dbpassword=c.fetchone()
            print dbpassword
            
            if str("(u'"+password+"',)") == str(dbpassword):
                conn.commit()
                c.close()
                conn.close()
                return '登录成功!'
            else:
                conn.commit()
                c.close()
                conn.close()
                return '登录失败!'
    if userexist==0:
        conn.commit()
        c.close()
        conn.close()
        return '用户名不存在！'

#用户登录页面的
@route("/index")
def index():
    
    return template("index")

#下载页面的
@route("/get")
def get():
    
    return static_file("book.db","./",download="book.db")

#获取统计数据
@route("/get2")
def get2():
    
    return static_file("count.db",root='db',mimetype="*/*",download="count.db")

#获取每日上传数据
@route("/get3/<date:path>")
def get3(date):
    
    return static_file(date+"-count.db",root='db',mimetype="*/*",download=date+"-count.db")

#获取评论
@route("/getcomment/<isbn:path>")
def getcomment(isbn):
    
    return static_file(isbn+"-comment.db",root='db',mimetype="*/*",download=isbn+"-comment.db")

#获取求书列表
@route("/getaskbook")
def getaskbook():
    
    return static_file("askbook.db",root='db',mimetype="*/*",download="askbook.db")

#获取用户信息
@route("/getuserinfo/<username:path>")
def getuserinfo(username):
    
    return static_file(username+"-profile",root='user',mimetype="*/*",download=username+"-profile")

#获取用户名称
@route("/getusernickname/<username:path>")
def getusernickname(username):
    fh = open('./user/'+username+'-profile')
    name=fh.readline()   
    return name

#获取用户留言
@route("/getusermessage/<username:path>")
def getusermessage(username):
    
    return static_file(username+"-message.db",root='user',mimetype="*/*",download=username+"-message.db")

#获取用户留言次数
@route("/getmessagetimes/<username:path>")
def getmessagetimes(username):
    if os.path.exists('user/'+username+'-message.db')==True:
        conn = sqlite3.connect('user/'+username+'-message.db')
        c = conn.cursor()
        c.execute('select * from statics')
        times=len(c.fetchall())
        return str(times)
    else:
        return "0"

#获取用户头像
@route("/getavatar/<username:path>")
def getavatar(username):
    
    return static_file(username+".jpg",root='avatar',mimetype="*/*",download=username+".jpg")

#获取用户上传历史
@route("/getuserhistory/<username:path>")
def getuserinfo(username):
    
    return static_file(username+".db",root='db',mimetype="*/*",download=username+".db")

#获取用户评论历史
@route("/getusercomment/<username:path>")
def getusercomment(username):
    
    return static_file(username+"-comment.db",root='user',mimetype="*/*",download=username+"-comment.db")


#下载全数据
@route("/getroot")
def getroot():
    os.system("rm root.tar.gz")
    os.system("tar czvf root.tar.gz ./")
    return static_file("root.tar.gz","./",download="root.tar.gz")


#生成xls文件并提供下载
@route("/getxls")
def getxls():
    os.system("python newxls.py")
    return static_file("test.xls","./",download="test.xls")

#手工输入
@route("/handlogin",method="post")
def reset():
    username = request.forms.get("username")
    password = request.forms.get("password")
    title = request.forms.get("title")
    price = request.forms.get("price")
    isbn = request.forms.get("isbn")
    pubdate=request.forms.get("pubdate")
    publisher=request.forms.get("publisher")
    lasttime = request.forms.get("time")
    print(username,password,isbn)
    exist=1#默认插入
    print exist
    if username =='admin' and password =='admin':
        #判断是否已经存在
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        for isbncode in c.execute('select code from book'):
            if str("(u'"+isbn+"',)") == str(isbncode):
                exist=0
        print exist
        if exist==0:
            #update()
            status="已更新原有记录。"
            conn = sqlite3.connect('book.db')
            c = conn.cursor()
            c.execute("update book set title='"+title+"' where code Like "+isbn+"")
            c.execute("update book set price='"+price+"' where code Like "+isbn+"")
            c.execute("update book set publisher='"+publisher+"' where code Like "+isbn+"")
            c.execute("update book set pubdate='"+pubdate+"' where code Like "+isbn+"")
            c.execute("update book set lasttime='"+lasttime+"' where code Like "+isbn+"")
            conn.commit()
            c.close()
            conn.close()
        else:
            #insert()
            status="已插入新的记录。"
            conn = sqlite3.connect('book.db')
            c = conn.cursor()
            c.execute("insert into book values ('"+title+"','"+price+"','"+publisher+"','"+isbn+"','"+pubdate+"','"+lasttime+"','0')")
            conn.commit()
            c.close()
            conn.close()
            
        return '登录成功：'+status
    else :
        return username+'登录失败'

#手工输入页面
@route("/hand")
def resetpage():
    return template("hand")  



#重置数据库
@route("/reset",method="post")
def reset():
    username = request.forms.get("username")
    password = request.forms.get("password")
    if username =='admin' and password =='admin':
        conn = sqlite3.connect('book.db')
        c = conn.cursor()
        c.execute("delete from book")
        conn.commit()
        c.close()
        conn.close()
        return '数据库已清空！'
    else:
        return '密码错误！'

#重置数据库登录
@route("/resetpage")
def resetpage():
    return template("reset")

#查询界面
@route("/query")
def query():
    os.system("python query.py")
    return static_file("out.html",root='static')       

@route('/static/<filepath:path>')
def server_static(filepath):
    return static_file(filepath, root='static')

#插入
def insert(isbn,lasttime,bookname,username):
    #params = isbn
    f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+isbn)
    jresult=f.read()
    jsonVal = json.loads(jresult)
    bookcode=jsonVal["isbn13"]
    bookname=jsonVal["title"]
    bookprice=jsonVal["price"]
    bookpublisher=jsonVal["publisher"]
    bookpubdate=jsonVal["pubdate"]
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("insert into book values ('"+bookname+"','"+bookprice+"','"+bookpublisher+"','"+bookcode+"','"+bookpubdate+"','"+lasttime+"','0')")
    conn.commit()
    c.close()
    conn.close()
    conn = sqlite3.connect('db/'+username+'.db')
    c = conn.cursor()
    c.execute("insert into book values ('"+bookname+"','"+bookprice+"','"+bookpublisher+"','"+bookcode+"','"+bookpubdate+"','"+lasttime+"','0')")
    conn.commit()
    c.close()
    conn.close()
    createdb(isbn,lasttime,bookname,username)

def createdb(bookcode,lasttime,bookname,username):
    #新建立以isbn号为名的统计点赞的数据库
    if os.path.exists('db/'+bookcode+"-praise.db")==False:
        conn = sqlite3.connect('db/'+bookcode+'-praise.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (who, time, praise)")
        #c.execute("insert into statics values ('admin','"+lasttime+"','0')")
        conn.commit()
        c.close()
        conn.close()
    #新建立以isbn号为名的统计评论的数据库
    if os.path.exists('db/'+bookcode+"-comment.db")==False:
        conn = sqlite3.connect('db/'+bookcode+'-comment.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (who, time, comment)")
        #c.execute("insert into statics values ('admin','"+lasttime+"','0')")
        conn.commit()
        c.close()
        conn.close()
    #新建立以isbn号为名的统计谁上传的数据库
    if os.path.exists('db/'+bookcode+"-who.db")==False:
        conn = sqlite3.connect('db/'+bookcode+'-who.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (who, time)")
        c.execute("insert into statics values ('"+username+"','"+lasttime+"')")
        conn.commit()
        c.close()
        conn.close()
    else:
        conn = sqlite3.connect('db/'+bookcode+'-who.db')
        c = conn.cursor()
        c.execute("insert into statics values ('"+username+"','"+lasttime+"')")
        conn.commit()
        c.close()
        conn.close()
    #建立次数统计数据库,将作为前端加载对象
    if os.path.exists('db/count.db')==False:
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (isbn, praise, comment,bookname,username,time)")
        c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"','"+lasttime+"')")
        conn.commit()
        c.close()
        conn.close()
    else:
        #上传后添加数据
        exist=1#默认插入
        #判断是否已经存在
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        for isbncode in c.execute('select isbn from statics'):
            if str("(u'"+bookcode+"',)") == str(isbncode):
                exist=0
                c.execute("update statics set time='"+lasttime+"' where isbn Like "+bookcode)
                conn.commit()
                c.close()
                conn.close()
                break
        print exist
        if exist==1:
            c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"','"+lasttime+"')")
            conn.commit()
            c.close()
            conn.close()
    #按日期次数统计数据库,将作为前端加载对象
    today=time.strftime('%Y%m%d',time.localtime())
    if os.path.exists('db/'+today+'-count.db')==False:
        conn = sqlite3.connect('db/'+today+'-count.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (isbn, praise, comment,bookname,username,time)")
        c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"','"+lasttime+"')")
        conn.commit()
        c.close()
        conn.close()
    else:
        #上传后添加数据
        exist=1#默认插入
        #判断是否已经存在
        conn = sqlite3.connect('db/'+today+'-count.db')
        c = conn.cursor()
        for isbncode in c.execute('select isbn from statics'):
            if str("(u'"+bookcode+"',)") == str(isbncode):
                exist=0
                conn.commit()
                c.close()
                conn.close()
                break
        print exist
        if exist==1:
            c.execute("insert into statics values ('"+bookcode+"','0','0','"+bookname+"','"+username+"','"+lasttime+"')")
            conn.commit()
            c.close()
            conn.close()

    return '操作成功'


#点赞
@route("/praise")
def praise():

    return template("praise")

#处理点赞
@route("/addpraise",method="post")
def addpraise():
    isbn = request.forms.get("isbn")
    username = request.forms.get("username")
    time = request.forms.get("time")
    praise = request.forms.get("praise")
    print praise,time
    
    #处理统计数据    
    if os.path.exists('db/count.db')==True:
        if getwhopraise(isbn,username)=="0":
            #没点过则加点赞数
            conn = sqlite3.connect('db/count.db')
            c = conn.cursor()
            c.execute("select praise from statics where isbn like "+isbn)
            num=c.fetchone()
            total=int(num[0])+1
            c.execute("update statics set praise='"+str(total)+"' where isbn Like "+isbn+"")
            conn.commit()
            c.close()
            conn.close()
        
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        if getwhopraise(isbn,username)=="0":
            #更新以isbn号为名的点赞数据库
            conn = sqlite3.connect('db/'+isbn+'-praise.db')
            c = conn.cursor()
            c.execute("insert into statics values ('"+username+"','"+time+"','"+praise+"')")
            conn.commit()
            c.close()
            conn.close()
            return "点赞成功"
        else:
            return "你点过赞了"

#获得点赞数-单次查询用
@route("/praise/<isbn:path>")
def getpraise(isbn):
    total=0
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        conn = sqlite3.connect('db/'+isbn+'-praise.db')
        c = conn.cursor()
        for praise in c.execute('select praise from statics'):
            print praise
            if praise==(u'null',):
                pass
            else:
                total=total+int(praise[0])
                print total
            #total=total+praise
        conn.commit()
        c.close()
        conn.close()
        return str(total)
    else:
        return "还没有此数据库！"

#有没有点过赞-单次查询用
@route("/whopraise/<isbn:path>/<name:path>")
def getwhopraise(isbn,name):
    if os.path.exists('db/'+isbn+"-praise.db")==True:
        conn = sqlite3.connect('db/'+isbn+'-praise.db')
        c = conn.cursor()
        for who in c.execute('select who from statics'):
            print who
            if name==who[0]:
                return "1" #点过了
        conn.commit()
        c.close()
        conn.close()
        return "0"

#评论
@route("/comment")
def comment():

    return template("comment")

#处理评论
@route("/addcomment",method="post")
def addcomment():
    isbn = request.forms.get("isbn")
    username = request.forms.get("username")
    time = request.forms.get("time")
    comment = request.forms.get("comment")
    print comment,time

    #处理统计数据    
    if os.path.exists('db/count.db')==True:
        #加评论数
        conn = sqlite3.connect('db/count.db')
        c = conn.cursor()
        c.execute("select comment from statics where isbn like "+isbn)
        num=c.fetchone()
        total=int(num[0])+1
        c.execute("update statics set comment='"+str(total)+"' where isbn Like "+isbn+"")
        conn.commit()
        c.close()
        conn.close()
        
    if os.path.exists('db/'+isbn+"-comment.db")==True:
        #更新以isbn号为名的评论数据库
        conn = sqlite3.connect('db/'+isbn+'-comment.db')
        c = conn.cursor()
        c.execute("insert into statics values ('"+username+"','"+time+"','"+comment+"')")
        conn.commit()
        c.close()
        conn.close()

    return "评论成功！"


#用户信息
@route("/user/profile")
def userprofile():

    return template("userprofile")

#存储用户信息-用post保证数据传送完整
@route("/user/editprofile",method="post")
def editprofile():
    username = request.forms.get("username")
    avatar = request.forms.get("avatar")
    nickname= request.forms.get("nickname")
    phone = request.forms.get("phone")
    address = request.forms.get("address")
    major = request.forms.get("major")
    sex = request.forms.get("sex")
    age = request.forms.get("age")
    
    fw=open('./user/'+username+'-profile','w') #打开一个空白文本文件，准备写入
    fw.write(nickname+'\n')
    fw.write(phone+'\n')
    fw.write(address+'\n')
    fw.write(major+'\n')
    fw.write(sex+'\n')
    fw.write(age+'\n')
    fw.flush()
    fw.close
    return "保存成功"

#头像上传
@route('/user/avatar')
def avatar():
    return template("upload")

@route('/user/avatarupload', method='POST')
def do_avatarupload():
    #username = request.forms.get('username')
    upload = request.files.get('upload')
    name, ext = os.path.splitext(upload.filename)
    if ext not in ('.png','.jpg','.jpeg'):
        return "File extension not allowed."

    save_path = "./avatar/"
    if not os.path.exists(save_path):
        os.makedirs(save_path)

    file_path = "{path}/{file}".format(path=save_path, file=upload.filename)
    if os.path.exists(file_path)==True:
        os.remove(file_path)
    upload.save(file_path)
    return "File successfully saved to '{0}'.".format(save_path)

#评论上传
@route('/user/comment')
def avatar():
    return template("syncomment")

@route('/user/commentupload', method='POST')
def do_commentupload():
    upload = request.files.get('upload')
    name, ext = os.path.splitext(upload.filename)
    if ext not in ('.db'):
        return "File extension not allowed."

    save_path = "./user/"
    if not os.path.exists(save_path):
        os.makedirs(save_path)

    file_path = "{path}/{file}".format(path=save_path, file=upload.filename)
    if os.path.exists(file_path)==True:
        os.remove(file_path)
    upload.save(file_path)
    return "File successfully saved to '{0}'.".format(save_path)

#修改
def update(isbn,lasttime,bookname, username):
    #print "Not changed"
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    c.execute("update book set lasttime='"+lasttime+"' where code Like "+isbn+"")
    conn.commit()
    c.close()
    conn.close()
    createdb(isbn,lasttime,bookname, username)


def find():
    conn = sqlite3.connect('book.db')
    c = conn.cursor()
    for isbncode in c.execute('select code from book'):
        print isbncode

#得到图片
@route("/getimage/<isbn:path>")
def getimage(isbn):
    f = urllib.urlopen("https://api.douban.com/v2/book/isbn/:"+isbn)
    jresult=f.read()
    jsonVal = json.loads(jresult)
    link=jsonVal["image"]  
    data = urllib.urlretrieve(link,"./images/"+isbn+".jpg")
    return static_file(isbn+".jpg",root='images',mimetype="*/*",download=isbn+".jpg")

#得到json
@route('/getjson', method='POST')    
def getjson():
    itemnumber = request.forms.get("itemnumber")
    page = request.forms.get("page")
    queryorder= request.forms.get("queryorder")
    keyword= request.forms.get("keyword")
    searchtype= request.forms.get("searchtype")
    itemnumber=int(itemnumber)
    page=int(page)
    queryorder=int(queryorder)
    searchtype=int(searchtype)
    result=[]
    dbpath='db/count.db'
    if searchtype == 0:
        dbpath='db/count.db'
    elif searchtype == 1:
        dbpath='db/idle.db'
        
    conn = sqlite3.connect(dbpath)
    c = conn.cursor()
    SqlSentence="SELECT * FROM statics"
    if queryorder == 0:
        SqlSentence="SELECT * FROM statics"
    elif queryorder == 1:
	SqlSentence="SELECT * FROM statics Order by comment desc"
    elif queryorder == 2:
	SqlSentence="SELECT * FROM statics Order by time desc"
    elif queryorder == 3:
	SqlSentence="SELECT * FROM statics WHERE bookname like '%"+keyword+"%'"
    
    i=0
    j=0
    #查上传过的书 
    if searchtype==0:
        for row in c.execute(SqlSentence):
            if i==(page-1)*itemnumber+j and j<itemnumber:
                j=j+1
                isbn=row[0]
                praise=row[1]
                comment=row[2]
                bookname=row[3]
                username=row[4]
                time=row[5]
                single={"isbn":str(isbn),
                       "praise":str(praise),
                       "comment":str(comment),
                       "bookname":str(bookname.encode("utf-8")),
                       "username":str(username),
                        "time":str(time)}
                result.append(single)
            if j==itemnumber:
                break
            i=i+1
    #查闲置书
    elif searchtype==1:    
        for row in c.execute(SqlSentence):
            if i==(page-1)*itemnumber+j and j<itemnumber:
                j=j+1
                bookname=row[0]
                isbn=row[1]
                purpose=row[2]
                price=row[3]
                detail=row[4]
                username=row[5]
                pubtime=row[6]
                finished=row[7]
                single={"bookname":str(bookname.encode("utf-8")),
                       "isbn":str(isbn),
                       "purpose":str(purpose),
                       "price":str(price.encode("utf-8")),
                       "detail":str(detail.encode("utf-8")),
                       "username":str(username),
                       "pubtime":str(pubtime),
                       "finished":str(finished)}
                result.append(single)
            if j==itemnumber:
                break
            i=i+1
        
    conn.commit()
    c.close()
    conn.close()
    #print result
    out=json.dumps(result, ensure_ascii=False) 
    #print i
    return str(out)

#获取json的发送界面
@route("/choosejson")
def choosejson():

    return template("getjson")

#得到按天查询json
@route('/getdailyjson', method='POST')    
def getdailyjson():
    itemnumber = request.forms.get("itemnumber")
    page = request.forms.get("page")
    date = request.forms.get("date")
    itemnumber=int(itemnumber)
    page=int(page)
    result=[]
    dbpath='db/'+date+'-count.db'
    conn = sqlite3.connect(dbpath)
    c = conn.cursor()
    SqlSentence="SELECT * FROM statics"

    i=0
    j=0
    for row in c.execute(SqlSentence):
        if i==(page-1)*itemnumber+j and j<itemnumber:
            j=j+1
            isbn=row[0]
            praise=row[1]
            comment=row[2]
            bookname=row[3]
            username=row[4]
            time=row[5]
            single={"isbn":str(isbn),
                   "praise":str(praise),
                   "comment":str(comment),
                   "bookname":str(bookname.encode("utf-8")),
                   "username":str(username),
                    "time":str(time)}
            result.append(single)
        if j==itemnumber:
            break
        i=i+1
    conn.commit()
    c.close()
    conn.close()
    #print result
    out=json.dumps(result, ensure_ascii=False) 
    #print i
    return str(out)

#获取按天查询的json的发送界面
@route("/choosedailyjson")
def choosedailyjson():

    return template("getdailyjson")

#处理空闲书列表
@route('/idlepublish', method='POST')
def idlepublish():
    username = request.forms.get("username")
    password = request.forms.get("password")
    bookname = request.forms.get("bookname")
    isbn = request.forms.get("isbn")
    purpose = request.forms.get("purpose")
    price = request.forms.get("price")
    detail = request.forms.get("detail")
    pubtime = request.forms.get("pubtime")
    if auth()=='登录成功!':
        if os.path.exists('db/idle.db')==False:
            conn = sqlite3.connect('db/idle.db')
            c = conn.cursor()
            c.execute("CREATE TABLE statics (bookname, isbn, purpose, price, detail, username, pubtime, finished)")
            c.execute("insert into statics values ('"+bookname+"','"+isbn+"','"+purpose+"','"+price+"','"+detail+"','"+username+"','"+pubtime+"','0')")
            conn.commit()
            c.close()
            conn.close()
        else:
            #上传后添加数据
            exist=1#默认插入
            #判断是否已经存在
            conn = sqlite3.connect('db/idle.db')
            c = conn.cursor()
            c.execute("insert into statics values ('"+bookname+"','"+isbn+"','"+purpose+"','"+price+"','"+detail+"','"+username+"','"+pubtime+"','0')")
            conn.commit()
            c.close()
            conn.close()

        return '操作成功'
    else:
        return '登录失败'

#空闲书发布界面
@route("/idlebook")
def idlebook():

    return template("idlebook")


#借让完成
@route('/endpublish', method='POST')
def endpublish():
    username = request.forms.get("username")
    password = request.forms.get("password")
    isbn = request.forms.get("isbn")
    if auth()=='登录成功!':
        if os.path.exists('db/idle.db')==True:
            conn = sqlite3.connect('db/idle.db')
            c = conn.cursor()
            c.execute("update statics set finished='1' where isbn Like "+isbn+" and username Like "+username)
            conn.commit()
            c.close()
            conn.close()
        return '操作成功'
    else:
        return '登录失败'

#处理借让完成情况
@route("/notidle")
def notidle():

    return template("notidle")

#处理求书列表
@route('/askforbook', method='POST')
def askforbook():
    username = request.forms.get("username")
    password = request.forms.get("password")
    bookname = request.forms.get("bookname")
    detail = request.forms.get("detail")
    if auth()=='登录成功!':
        if os.path.exists('db/askbook.db')==False:
            conn = sqlite3.connect('db/askbook.db')
            c = conn.cursor()
            c.execute("CREATE TABLE statics (bookname, detail, username, pubtime, finished)")
            c.execute("insert into statics values ('"+bookname+"','"+detail+"','"+username+"','"+pubtime+"','0')")
            conn.commit()
            c.close()
            conn.close()
        else:
            #上传后添加数据
            exist=1#默认插入
            #判断是否已经存在
            conn = sqlite3.connect('db/askbook.db')
            c = conn.cursor()
            c.execute("insert into statics values ('"+bookname+"','"+detail+"','"+username+"','"+pubtime+"','0')")
            conn.commit()
            c.close()
            conn.close()

        return '操作成功'
    else:
        return '登录失败'

#求书界面
@route("/askbook")
def askbook():

    return template("askbook")

#删除求书记录
@route("/deleteaskbook/", method="POST")
def deleteaskbook():
    username = request.forms.get("username")
    pubtime = request.forms.get("pubtime")
    if os.path.exists('db/askbook.db')==True:
        conn = sqlite3.connect('db/askbook.db')
        c = conn.cursor()
        c.execute("delete from statics where pubtime Like "+pubtime+" and username Like "+username+"")
        conn.commit()
        c.close()
        conn.close()
        return "操作成功"
    else:
        return "数据不存在"
    
#删除求书界面
@route("/deleteaskbookpage")
def deleteaskbookpage():

    return template("deleteaskbook")

#处理心情
@route("/everydaymood", method="POST")
def everydaymood():
    username = request.forms.get("username")
    pubtime = request.forms.get("pubtime")
    mood = request.forms.get("mood")
    words = request.forms.get("words")
    date = request.forms.get("date")
    if os.path.exists('db/mood.db')==False:
        conn = sqlite3.connect('db/mood.db')
        c = conn.cursor()
        c.execute("CREATE TABLE statics (username, time, mood, words, date)")
        c.execute("insert into statics values ('"+username+"','"+pubtime+"','"+mood+"','"+words+"','"+date+"')")
        conn.commit()
        c.close()
        conn.close()
        return "发布成功"
    else:
        exist=1#默认插入
        #判断是否已经存在
        conn = sqlite3.connect('db/mood.db')
        c = conn.cursor()
        for who in c.execute('select username from statics where date like '+date):
            if str("(u'"+username+"',)") == str(who):
                exist=0
                conn.commit()
                c.close()
                conn.close()
                break
        if exist==1:
            conn = sqlite3.connect('db/mood.db')
            c = conn.cursor()
            c.execute("insert into statics values ('"+username+"','"+pubtime+"','"+mood+"','"+words+"','"+date+"')")
            conn.commit()
            c.close()
            conn.close()
            return "发布成功"
        else:
            return "你今天已经发布过了。"
    
#发布心情
@route("/pubmood")
def pubmood():

    return template("pubmood")

#得到心情json
@route('/getmoodjson', method='POST')    
def getmoodjson():
    itemnumber = request.forms.get("itemnumber")
    page = request.forms.get("page")
    itemnumber=int(itemnumber)
    page=int(page)
    result=[]
    dbpath='db/mood.db'
    conn = sqlite3.connect(dbpath)
    c = conn.cursor()
    SqlSentence="SELECT * FROM statics Order By time desc"

    i=0
    j=0
    for row in c.execute(SqlSentence):
        if i==(page-1)*itemnumber+j and j<itemnumber:
            j=j+1
            username=row[0]
            time=row[1]
            mood=row[2]
            words=row[3]
            single={"username":str(username.encode("utf-8")),
                   "time":str(time.encode("utf-8")),
                   "mood":str(mood.encode("utf-8")),
                   "words":str(words.encode("utf-8"))}
            result.append(single)
        if j==itemnumber:
            break
        i=i+1
    conn.commit()
    c.close()
    conn.close()
    out=json.dumps(result, ensure_ascii=False) 
    return str(out)

#获取按天查询的json的发送界面
@route("/moodjson")
def moodjson():

    return template("moodjson")

#用户个人留言板
@route("/user/message", method="POST")
def messagedb():
    tusername = request.forms.get("tusername")
    username = request.forms.get("username")
    time = request.forms.get("time")
    words = request.forms.get("words")
    if os.path.exists("user/"+tusername+"-message.db")==False:
        conn = sqlite3.connect("user/"+tusername+"-message.db")
        c = conn.cursor()
        c.execute("CREATE TABLE statics (username, time, words)")
        c.execute("insert into statics values ('"+username+"','"+time+"','"+words+"')")
        conn.commit()
        c.close()
        conn.close()
    else:
        conn = sqlite3.connect("user/"+tusername+"-message.db")
        c = conn.cursor()
        c.execute("insert into statics values ('"+username+"','"+time+"','"+words+"')")
        conn.commit()
        c.close()
        conn.close()
    return "评论成功！"
        
#给用户留言
@route("/leavemessage")
def leavemessage():

    return template("leavemessage")

#删除留言
@route("/deletemessage/", method="POST")
def deletemessage():
    username = request.forms.get("username")
    password = request.forms.get("password")
    tusername = request.forms.get("tusername")
    time = request.forms.get("time")
    if auth()=='登录成功!':
        if os.path.exists('user/'+username+'-message.db')==True:
            conn = sqlite3.connect('user/'+username+'-message.db')
            c = conn.cursor()
            c.execute("delete from statics where time Like "+time+" and username Like "+tusername+"")
            conn.commit()
            c.close()
            conn.close()
            return "操作成功"
        else:
            return "数据不存在"
    else :
        return username+'登录失败'
#删除留言界面
@route("/deletemessagepage")
def deletemessagepage():

    return template("deletemessage")
#@route('/hello/:name')
#def index(name='World'):
#    return '<b>Hello %s!</b>' % name


#默认端口  run(host='localhost', port=8080)
run(host='127.0.0.1', port=8080)
