import urllib2, unicodedata
from bs4 import BeautifulSoup, SoupStrainer
import re
import pdb
import os
import wget
import itertools
import multiprocessing
## multi core baby
pool = multiprocessing.Pool()

####
# Output of 
# this script will be a TSV
# with the following information
####
YEAR = "2013"
SEMESTER = "fall"
OUTPUT = "listOfClasses.tsv"
HEADERS = ["Department", "CourseNumber", "Title", "CRN", "Type", "Session",
    "Time", "Days", "Location", "Instructor", "Credits", "Gen Ed"]

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
  subjectCode, number, courseTitle = info
  url = serverURL  +"/" + subjectCode + "/" + number
  page = loadHTMLURL(url)
  credits = findCredit(page)
  genEds = findGenEd(page)
  classAttributes = findClassInfo(page)
  additionalInfo = [subjectCode, number, courseTitle] 
  return map(lambda x:additionalInfo + x + credits + genEds, classAttributes)

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
    return [" ".join(normalize)]
  else:
    return [""]

def findCredit(page):
  p = page.find_all("p", {"class", "portlet-padtop10"})
  credits = filter(lambda x:"Credit" in x.prettify(), p)
  return map(lambda x: x.text.split()[1], credits)

def findClassInfo(page):
  divs = page.find_all("div", {"class", "section-meeting"})
  allClassInfo = map(normalizeString , divs)
  split =  split_list(allClassInfo, len(allClassInfo)/8)
  return map(lambda x:x[2:], split)

subjectPage = loadHTMLURL(serverURL)
departmentLinks = subjectPage.find_all("td")
departments = map(lambda x:x.get("title"), departmentLinks)
depNames = filter(lambda x:x != None, departments)
depUrl = map(lambda x: serverURL + "/" + x, depNames)
pages = map(loadHTMLURL, depUrl)
classes = map(getClassesAttributes, pages)
with open(OUTPUT, "w") as f:
  f.write("\t".join(HEADERS) + "\n")
  map(lambda x:f.write("\t".join(x) + "\n"), flatten(classes))
