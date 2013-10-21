import urllib2, unicodedata
from bs4 import BeautifulSoup, SoupStrainer
import re
import pdb
import os
import wget
import itertools
import multiprocessing
import json
## multi core baby

####
# Output of 
# this script will be a TSV
# with the following information
####
YEAR = "2012"
SEMESTER = "fall"
OUTPUT = "listOfClasses.json"

serverURL = "https://courses.illinois.edu/cisapp/dispatcher/schedule/%s/%s" % (YEAR, SEMESTER)
normalizeString = lambda x:" ".join(x.text.split())

def flatten(lst):
  return list(itertools.chain(*lst))

def split_list(alist, wanted_parts=1):
  length = len(alist)
  return [ alist[i*length // wanted_parts: (i+1)*length // wanted_parts] 
    for i in range(wanted_parts) ]

def loadHTMLURL ( url ):
  page = urllib2.urlopen(url)
  page = page.read()
  return BeautifulSoup(page)

def getClassesAttributes(page):

  trs = page.find_all("tr")
  allClassCodes = map(lambda x:x.find_all("td"), trs)
  validClassInfo = filter(lambda x: len(x)>2, allClassCodes)
  # remove HTML
  names = map(lambda x:map(lambda y: y.text, x), validClassInfo)
  return flatten(map(scrapeClass, names))

def scrapeClass(info):
  try:
    subjectCode, number, courseTitle = info
    print subjectCode + " " + number
    url = serverURL  +"/" + subjectCode + "/" + number
    page = loadHTMLURL(url)
    credits = findCredit(page)
    genEds = findGenEd(page)
    classAttributes = findClassInfo(page)
    additionalInfo = {"dep": subjectCode, "courseNumber": int(number),
        "title": courseTitle, "credits": credits}
    return map(lambda x:dict(additionalInfo.items() + x.items() + genEds.items()), classAttributes)
  except:
    return []

def findGenEd(page):
  p = page.find_all("p", {"class", "portlet-padtop10"})
  credits = filter(lambda x:"General Education" in x.prettify(), p)
  if(len(credits) > 0):
    genEds = credits[0].text.split("\n")[2:]
    trim = lambda x: " ".join(x.split())
    normalize = map(trim, genEds)
    normalize = filter(lambda x: len(x) > 2, normalize)
    normalize = map(lambda x:x.replace(":", "").replace("and", ""). replace("UIUC", "").replace("course",""), normalize)
    normalize = map(trim, normalize)
    return {"genEd": " ".join(normalize)}
  else:
    return {}

def findCredit(page):
  p = page.find_all("p", {"class", "portlet-padtop10"})
  credits = filter(lambda x:"Credit" in x.prettify(), p)
  return credits[0].text.split()[1]

def findClassInfo(page):
  def info(tr):
    trim = lambda x: " ".join(x.split())
    parse = lambda x,y: trim(tr.find_all("td", {"class": x})[y].text)
    return {"crn": int(parse("w50", 1)),
        "classType": parse("w80",0),
        "session": parse("w55",0),
        "time": parse("w75", 0),
        "days": parse("w55", 1),
        "location": parse("w120", 0),
        "instructor": parse("ie-table-width", 0)}
  trs = page.find_all("tr", {"class", "table-item"})
  return map(info, trs)

subjectPage = loadHTMLURL(serverURL)
departmentLinks = subjectPage.find_all("td")
departments = map(lambda x:x.get("title"), departmentLinks)
depNames = filter(lambda x:x != None, departments)
# depNames = ["AAS", "ZULU"]
depUrl = map(lambda x: serverURL + "/" + x, depNames)
pages = map(loadHTMLURL, depUrl)
classes = map(getClassesAttributes, pages)
with open(OUTPUT, "w") as f:
  map(lambda x: f.write(json.dumps(x) + "\n"), flatten(classes))
