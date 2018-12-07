**This project is not maintained anymore**

---

# europeana-static

1. Copy src/main/resources/template-context.xml src/main/resources/context.xml 

2. Change src/main/resources/context.xml 
    imageRepository should point to top of your image tree
    
  
Not strictly related to this proj, but since it bit me, this could save you some time...

JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
in /usr/share/tomcat7/bin/catalina.sh will help you if you get into 5-30 min timeouts during startups due to randomnes depletion
