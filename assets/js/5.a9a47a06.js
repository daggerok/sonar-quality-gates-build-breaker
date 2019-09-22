(window.webpackJsonp=window.webpackJsonp||[]).push([[5],{204:function(t,a,s){"use strict";s.r(a);var e=s(0),n=Object(e.a)({},(function(){var t=this,a=t.$createElement,s=t._self._c||a;return s("ContentSlotsDistributor",{attrs:{"slot-key":t.$parent.slotKey}},[s("h1",{attrs:{id:"sonarqube-build-breaker"}},[s("a",{staticClass:"header-anchor",attrs:{href:"#sonarqube-build-breaker","aria-hidden":"true"}},[t._v("#")]),t._v(" SonarQube Build Breaker")]),t._v(" "),s("p",[t._v("SonarQube Build Breaker designed to fail SonarQube analysis during CI pipeline build if quality grates not passed "),s("a",{attrs:{href:"https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker",target:"_blank",rel:"noopener noreferrer"}},[s("img",{attrs:{src:"https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker.svg?branch=master",alt:"Build Status"}}),s("OutboundLink")],1)]),t._v(" "),s("p"),s("div",{staticClass:"table-of-contents"},[s("ul",[s("li",[s("a",{attrs:{href:"#quick-start"}},[t._v("Quick start")]),s("ul",[s("li",[s("a",{attrs:{href:"#get-sonarqube-build-breaker"}},[t._v("Get SonarQube Build Breaker")])]),s("li",[s("a",{attrs:{href:"#do-analysis"}},[t._v("Do analysis")])])])]),s("li",[s("a",{attrs:{href:"#links"}},[t._v("Links")])])])]),s("p"),t._v(" "),s("h2",{attrs:{id:"quick-start"}},[s("a",{staticClass:"header-anchor",attrs:{href:"#quick-start","aria-hidden":"true"}},[t._v("#")]),t._v(" Quick start")]),t._v(" "),s("p",[s("em",[t._v("Easy as 1-2-3")])]),t._v(" "),s("p",[t._v("TODO...")]),t._v(" "),s("div",{staticClass:"language-yaml line-numbers-mode"},[s("pre",{pre:!0,attrs:{class:"language-yaml"}},[s("code",[s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("version")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token string"}},[t._v("'2.1'")]),t._v("\n"),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("networks")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n  "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("dev")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("driver")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" bridge\n"),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("volumes")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n  "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("conf")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n  "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("data")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n  "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("logs")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("{")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("}")]),t._v("\n"),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("services")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n  "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("sonar")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("image")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" sonarqube"),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("7.9.1"),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("-")]),t._v("community\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("ports")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("[")]),s("span",{pre:!0,attrs:{class:"token string"}},[t._v("'80:9000'")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("]")]),t._v("\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("networks")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("[")]),t._v("dev"),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("]")]),t._v("\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("volumes")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("[")]),s("span",{pre:!0,attrs:{class:"token string"}},[t._v("'conf:/opt/sonarqube/conf'")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n              "),s("span",{pre:!0,attrs:{class:"token string"}},[t._v("'data:/opt/sonarqube/data'")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),t._v("\n              "),s("span",{pre:!0,attrs:{class:"token string"}},[t._v("'logs:/opt/sonarqube/logs'")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(",")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("]")]),t._v("\n    "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("healthcheck")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("\n      "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("test")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" curl "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("-")]),t._v("f http"),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("//127.0.0.1"),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v("9000/ "),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("|")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v("|")]),t._v(" exit 1\n      "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("interval")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" 5s\n      "),s("span",{pre:!0,attrs:{class:"token key atrule"}},[t._v("retries")]),s("span",{pre:!0,attrs:{class:"token punctuation"}},[t._v(":")]),t._v(" "),s("span",{pre:!0,attrs:{class:"token number"}},[t._v("25")]),t._v("\n")])]),t._v(" "),s("div",{staticClass:"line-numbers-wrapper"},[s("span",{staticClass:"line-number"},[t._v("1")]),s("br"),s("span",{staticClass:"line-number"},[t._v("2")]),s("br"),s("span",{staticClass:"line-number"},[t._v("3")]),s("br"),s("span",{staticClass:"line-number"},[t._v("4")]),s("br"),s("span",{staticClass:"line-number"},[t._v("5")]),s("br"),s("span",{staticClass:"line-number"},[t._v("6")]),s("br"),s("span",{staticClass:"line-number"},[t._v("7")]),s("br"),s("span",{staticClass:"line-number"},[t._v("8")]),s("br"),s("span",{staticClass:"line-number"},[t._v("9")]),s("br"),s("span",{staticClass:"line-number"},[t._v("10")]),s("br"),s("span",{staticClass:"line-number"},[t._v("11")]),s("br"),s("span",{staticClass:"line-number"},[t._v("12")]),s("br"),s("span",{staticClass:"line-number"},[t._v("13")]),s("br"),s("span",{staticClass:"line-number"},[t._v("14")]),s("br"),s("span",{staticClass:"line-number"},[t._v("15")]),s("br"),s("span",{staticClass:"line-number"},[t._v("16")]),s("br"),s("span",{staticClass:"line-number"},[t._v("17")]),s("br"),s("span",{staticClass:"line-number"},[t._v("18")]),s("br"),s("span",{staticClass:"line-number"},[t._v("19")]),s("br"),s("span",{staticClass:"line-number"},[t._v("20")]),s("br")])]),s("h3",{attrs:{id:"get-sonarqube-build-breaker"}},[s("a",{staticClass:"header-anchor",attrs:{href:"#get-sonarqube-build-breaker","aria-hidden":"true"}},[t._v("#")]),t._v(" Get SonarQube Build Breaker")]),t._v(" "),s("p",[t._v("TODO...")]),t._v(" "),s("h3",{attrs:{id:"do-analysis"}},[s("a",{staticClass:"header-anchor",attrs:{href:"#do-analysis","aria-hidden":"true"}},[t._v("#")]),t._v(" Do analysis")]),t._v(" "),s("p",[t._v("TODO...")]),t._v(" "),s("h2",{attrs:{id:"links"}},[s("a",{staticClass:"header-anchor",attrs:{href:"#links","aria-hidden":"true"}},[t._v("#")]),t._v(" Links")]),t._v(" "),s("ul",[s("li",[s("a",{attrs:{href:"https://hub.docker.com/_/sonarqube/",target:"_blank",rel:"noopener noreferrer"}},[t._v("SonarQube docker image"),s("OutboundLink")],1)]),t._v(" "),s("li",[s("a",{attrs:{href:"https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/",target:"_blank",rel:"noopener noreferrer"}},[t._v("SonarScanner for Maven"),s("OutboundLink")],1)]),t._v(" "),s("li",[s("a",{attrs:{href:"https://github.com/SonarSource/sonar-scanning-examples/tree/master/sonarqube-scanner-maven",target:"_blank",rel:"noopener noreferrer"}},[t._v("SonarQube maven examples"),s("OutboundLink")],1)]),t._v(" "),s("li",[t._v("https://mitrai.com/tech-guide/using-sonarqube-to-automate-quality-checks-for-your-maven-project/")]),t._v(" "),s("li",[s("a",{attrs:{href:"https://github.com/daggerok/publish-maven-project-to-jcenter",target:"_blank",rel:"noopener noreferrer"}},[t._v("How to publish maven projects to JCenter"),s("OutboundLink")],1)])]),t._v(" "),s("p",[s("em",[t._v("NOTE, this project has been based on\n"),s("a",{attrs:{href:"https://github.com/daggerok/main-starter/tree/maven-java",target:"_blank",rel:"noopener noreferrer"}},[t._v("daggerok/main-starter"),s("OutboundLink")],1),t._v(",\n"),s("a",{attrs:{href:"https://github.com/daggerok/sonarqube-maven-poc",target:"_blank",rel:"noopener noreferrer"}},[t._v("daggerok/sonarqube-maven-poc"),s("OutboundLink")],1),t._v(", and\n"),s("a",{attrs:{href:"https://github.com/daggerok/publish-maven-project-to-jcenter",target:"_blank",rel:"noopener noreferrer"}},[t._v("daggerok/publish-maven-project-to-jcenter"),s("OutboundLink")],1),t._v("\nrepositories")])])])}),[],!1,null,null,null);a.default=n.exports}}]);