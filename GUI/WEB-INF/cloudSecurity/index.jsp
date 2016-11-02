<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.lang.*,GUI.*" %>
<%@ page import="com.mongodb.*" %>
<%@ page import="json.simple.*" %>
<%@ page import="gson.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<HTML>
<HEAD>
<TITLE>Cloudman</TITLE>
</HEAD>

<style>
.axis path, .axis line {
    fill: none;
    shape-rendering: crispEdges;
}
.axis text {
    font-family: sans-serif;
    font-size: 11px;
}

.info {
  stroke: steelblue;
}

div.tooltip {   
  position: absolute;      
  text-align: left;    
  line-height:1.8;
  vertical-align: middle;
           
  font: 13px sans-serif;  
  font-weight: bold;
  
  border: 0px;      
  border-radius: 8px;           
  
  float: left;  
  color: blue;
}

.default{
  fill: #4776B4;
  stroke: white;
//  stroke-width: 1.5;
  opacity: 0.7; 
}

.updatedRT{
  fill: #4776B4;
  opacity: 0.7; 
} 

.twinkle{
  fill: white;
  stroke: #C32F4B;
  stroke-width: 1.5;
  opacity: 0.8; 
}

.dead{
  fill: gray;
  opacity: 0.6; 
}
.leaf circle {
  fill: white;
  fill-opacity: 1;
}

.root circle {
  fill: #EAEAEA;
  stroke: white; 
  stroke-width: 3;

  opacity: 0.9;
}

.node {
  fill: #CCCCCC;
  stroke-width: 3;
}

.vm_node {
  fill: white; // dark green
  stroke: #990000; 
  stroke-width: 3;
  opacity: 0.9;
}

.vm_text{
  font-size: 9px;
  font-family: serif;
  stroke: black;
}

.deadText{
  font-size: 15px;
  font-family: serif;
  stroke: black;
}

text {
  font: 10px sans-serif;
}

.util{
  font-size:13px;
  font-family: serif;
  stroke: black;
}
.active circle {
  fill: white;
}

#Composite {
  fill: red;

}

#Concurrent {
  fill: orange;
}

#Distributed {
  fill: steelblue;
}

#Request {
  fill: white;
}

.Composite {
  stroke: navy;
  stroke-width: 3;
  opacity: 1;
}

.Concurrent {
  stroke: green;
  stroke-width: 3;
  opacity: 1;
}

.Distributed {
  stroke: orange;
  stroke-width: 3;
  opacity: 1;  
}

.Request {
  stroke: pink;
  stroke-width: 2;
  opacity: 0.5;
}

#container {
    margin:2%;
    padding:20px;
    border:2px solid #d0d0d0;
    border-radius: 5px;
}
}
  }
</style>
<body style="background:#EAEAEA;">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<form id="dataForm" >
	<input type="hidden" id="circlePM" value=""/>
	<input type="hidden" id="packPM" value=""/>
	<input type="hidden" id="linkPM" value=""/>
	<input type="hidden" id="changedPM" value=""/>
	<input type="hidden" id="changedLink" value=""/>
	<input type="hidden" id="deadPMs" value=""/>
	<input type="hidden" id="changedUtil" value=""/>
	<input type="hidden" id="changedRT" value=""/>
	<input type="hidden" id="pmRT" value=""/>	
	<input type="hidden" id="vmRT" value=""/>	
	<input type="hidden" id="historyRT" value=""/>	
	<input type="hidden" id="historyUtil" value=""/>
</form>

<%	
	cloudmanGUI gui = new cloudmanGUI();
	dbConnection db = new dbConnection();
	
	Integer numPM = db.getNumCollection();
%>



<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>

<script>
var historyIP = "history/history.jsp?ip=";

var timeInterval=7000;
var pageWidth = window.innerWidth,
    pageHeight = window.innerHeight;
    if (typeof pageWidth != "number"){
        if (document.compatMode == "CSS1Compat"){
            pageWidth = document.documentElement.clientWidth;
            pageHeight = document.documentElement.clientHeight;
        } else {
            pageWidth = document.body.clientWidth;
            pageHeight = document.body.clientHeight;
        }
  }

  
var diameter =  pageWidth;
var format = d3.format(",d");
var CurrentNode = "null";
var storeIP = new Set();

var pmIPs = [];
var deadPMs = [];
var pmStatus = [];

var w = diameter;
var h = pageHeight/1.2;
var padding = 0;
var num=<%=numPM+1%>;   //how many PM circles in the page
var offset = diameter/4;  //for the dependency graph central position

var clickedIP;
var graph2;
var pack;

var closeGroupFlag = 0;

function setflag(){
  if(CurrentNode!=="null"){
    storeIP.add(CurrentNode);
  }
}

function removeflag(){
  if(CurrentNode!=="null"){
    storeIP.delete(CurrentNode);
  }
}

var svg = d3.select("body")
    .append("svg")
    .attr("width", diameter)
    .attr("height",diameter/2)
    .append("g")
    .attr("transform", function (d){
      return "translate("+diameter/42+",0)";
    });

var focus_node = -1;

var old_util = null; // Helps to reduce unnecessary refresh
var old_graph = null;//Helps to reduce unnecessary refresh
var old_graph2 = null;//Helps to reduce unnecessary refresh

function ajaxCirclePM(){
	var ajaxResult="";    
	document.getElementById("circlePM").value="request";                   
	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});

	document.getElementById("circlePM").value=null; 
	return ajaxResult;
}

function ajaxPackPM(indexIP){
	var ajaxResult="";     
	document.getElementById("packPM").value=indexIP;                   
	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("packPM").value=null;      
	return ajaxResult;
}

function ajaxLinkPM(indexIP){
	var ajaxResult="";      
	document.getElementById("linkPM").value=indexIP;  
	
	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("linkPM").value=null;      
	return ajaxResult;
}

function ajaxChangedPM(){
	var ajaxResult="";     
	document.getElementById("changedPM").value="request";

	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("changedPM").value=null;      
	return ajaxResult;
}

function ajaxChangedLink(){
	var ajaxResult="";     
	document.getElementById("changedLink").value="request";

	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("changedLink").value=null;      
	return ajaxResult;
}

function ajaxDeadPM(){
	var ajaxResult="";
//	$(document).ready(function(){      
	document.getElementById("deadPMs").value="request";

	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("deadPMs").value=null;      
	return ajaxResult;
}

function ajaxChangedUtil(){
	var ajaxResult="";
//	$(document).ready(function(){      
	document.getElementById("changedUtil").value="request";

	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("changedUtil").value=null;      
	return ajaxResult;
}

function ajaxChangedRT(){
	var ajaxResult="";
//	$(document).ready(function(){      
	document.getElementById("changedRT").value="request";

	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("changedRT").value=null;      
	return ajaxResult;
}

function ajaxHistoryUtil(indexIP){
	var ajaxResult="";     
	document.getElementById("historyUtil").value=indexIP;     
	
	var msgCircle=$('#circlePM').val(); 
	var msgPack=$('#packPM').val(); 
	var msgLink=$('#linkPM').val(); 
	var msgChangedPM=$('#changedPM').val(); 
	var msgChangedLink=$('#changedLink').val(); 
	var msgDeadPM=$('#deadPMs').val(); 
	var msgUtil=$('#changedUtil').val(); 
	var msgRT=$('#changedRT').val(); 
	var msgHistoryRT=$('#historyRT').val(); 
	var msgHistoryUtil=$('#historyUtil').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{circlePM:msgCircle,packPM:msgPack,linkPM:msgLink,changedPM:msgChangedPM,changedLink:msgChangedLink,deadPMs:msgDeadPM,changedUtil:msgUtil,changedRT:msgRT,historyRT:msgHistoryRT,historyUtil:msgHistoryUtil},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("historyUtil").value=null;      
	return ajaxResult;
}

function closeGroup(){
	d3.selectAll("line").remove();
	d3.selectAll("marker").remove();
	d3.selectAll("path").remove();	
	d3.selectAll(".util").remove();
	d3.selectAll(".vm_text").remove();
	d3.selectAll(".vm_node").remove();
	d3.selectAll(".node").remove();
	d3.selectAll(".closeText").remove();
	d3.selectAll(".closeButton").remove();
	
	closeGroupFlag = 1;
}

var dataset = ajaxCirclePM();
//console.log(dataset);

var cpuScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return d.cpu; })])
    .range([1, 10]);

var memScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return d.mem; })])
    .range([1, 10]);

var diskScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return d.disk; })])
    .range([1, 10]);

var netinScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return d.net_in; })])
    .range([1, 10]);

var netoutScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return d.net_out; })])
     .range([1, 10]);

var xScale = d3.scale.linear()
    .domain([0, num])
    .range([padding, w - padding*2]);	
	
var yScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out); })])
    .range([diameter/6,0])


//r is the size of circle
var rScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out); })])
    .range([diameter/256, diameter/20]);
	
//start to draw the circles
svg.selectAll("circle")
    .data(dataset)
    .enter()
    .append("circle")
    .attr("class", "default")
    .attr("id", function(d) {return d.ip;})
    .attr("cx", function(d,i) {
        return xScale(i);
    })
    .attr("cy", function(d) {		
        return h-rScale(cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out))*4;
    })
    .attr("r", function(d) {
        return rScale(cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out));
    }).attr("fill", "#4776B4");

// deadPM	
	dataset.forEach(function (d){
          pmIPs.push(d.ip);
		  pmStatus.push(d.status);
    });
	
	for(var i=0;i<pmIPs.length;i++){
		if(pmStatus[i] == "dead")
			deadPMs.push(pmIPs[i]);
	}

//	console.log(deadPMs);
	
    for(var i=0;i<deadPMs.length;i++){
       d3.selectAll("circle").filter(function (d){
          if(d3.select(this).attr("id")===deadPMs[i])
             return true;
          else
             return false;
       }).attr("class","dead")
         .style("fill","gray")
//		 .attr("cy", h-rScale(0))
//		 .attr("r", rScale(0))
		 .attr("opacity","0.6");
     }	
    
	svg.selectAll("circle")
    .data(dataset)
	.on("click", function(d,i) {
	
	if(d3.select(this).attr("class")==="dead")
	{
		d3.selectAll(".deadText").remove();
		closeGroupFlag = 1;
		closeGroup();
		
		svg.append("text")
			  .attr("class","deadText")
			  .attr("x", xScale(i)-(w-padding*2)/num/2)
			  .attr("y", h+(rScale(0)*3))
			  .text("ID : " + d.id);
			
		return false;
	}
	
	closeGroupFlag = 0;
		
	d3.selectAll(".util").remove();
	
     d3.selectAll("circle").filter(function (d){
      var currentclass = d3.select(this).attr("class");
	  d3.select(this).transition().style("fill","#4776B4").style("stroke-width","1.5px").style("stroke","white").attr("opacity","0.7");
	  
      if(currentclass === "show")
        return true;
      else
        return false;
    }).attr("class","default").style("fill","#4776B4").style("stroke-width","1.5px").style("stroke","white").attr("opacity","0.7").transition();
	
    d3.select(this).attr("class","show");
    d3.select(this).style("stroke","#990000").style("stroke-width","4.5px").attr("opacity","0.7").transition();
 
 	dataset.forEach(function (d){
          pmIPs.push(d.ip);
    });
	
 //This part is to add an information log below the convass to show related info
    focus_node = d.ip;
    CurrentNode = d.ip;
	
    var currentdata = [d.ip, d.id, d.rt, d.cpu, d.mem, d.disk, d.net_in, d.net_out];
    var operation = svg.selectAll(".util").data(currentdata);

operation.enter().append("text")
      .attr("class","util")
      .attr("x",function (d){
        return diameter/5.7;
      })
      .attr("y", function (d,i){
        return (diameter/17+(diameter/64)*i);
      })
      .text(function (d,i){		
        if(i===1)
          return "ID: "+d;
		else if(i===2)
		  return "Delay: "+d + " ms"; 	  
		else if(i===3)
		  return "CPU: "+d + " %";
		else if(i===4)
		  return "RAM: "+d + " %";
		else if(i===5)
		  return "Disk: "+d + " %";
		else if(i===6)
		  return "Inbound: "+d + " %";  
		else if(i===7)
		  return "Outbound: "+d + " %";   
		  
     });

    //This part is to manipulate the green circles.
     d3.selectAll("circle").filter(function (d){
      var currentclass = d3.select(this).attr("class");
	  d3.select(this).transition().style("fill","#4776B4").style("stroke-width","1.5px").style("stroke","white").attr("opacity","0.7");
	  
      if(currentclass === "show")
        return true;
      else
        return false;
    }).attr("class","default").style("fill","#4776B4").style("stroke-width","1.5px").style("stroke","white").attr("opacity","0.7").transition();
	
    d3.select(this).attr("class","show");
    d3.select(this).style("stroke","#990000").style("stroke-width","4.5px").attr("opacity","0.7").transition();


    //************This part is to delete any detailed circles due to previous click events**********************
    var focus = d3.select(this);
    var currentPM = "new "+focus.attr("id");

    var xpos = focus.attr("cx");
    var ypos = focus.attr("cy");
    var old_r = focus.attr("r");

    d3.selectAll("circle")
    .filter(function (d){
      var currentclass = d3.select(this).attr("class");
      if(currentclass === "vm_node")
        return true;
      else
        return false;
    })
    .remove();

    d3.selectAll("line").remove();

    d3.selectAll("path").remove();

    d3.selectAll("text").filter(function (d){
      var currentclass = d3.select(this).attr("class");
      if((currentclass==="util")||(currentclass==="closeText"))
        return false;
      else
        return true;
    })
    .remove();

    d3.selectAll("marker").remove();

    d3.selectAll("g").filter(function (d){
      var currentclass = d3.select(this).attr("class");
      if((currentclass === "node")||(currentclass ==="leaf node")||(currentclass ==="root node"))
        return true;
      else
        return false;
    })
    .remove();


    storeIP.forEach(function (d){
        var ip = d;
        
        d3.selectAll("circle").filter(function (d, i){
          var currentID = d3.select(this).attr("id");

          if(ip===currentID){
            return true;
          }
          else
            return false;
        }).style("fill","black");
      }); 
	  
	 var close = svg.append("rect")
		.attr("class","closeButton")
		.attr("x", w/2 + w/8.5)
		.attr("y", w/25)
		.attr("width", 30)
		.attr("height", 30)
		.style("stroke", "white")
		.style("fill", "#990000")
		.on("click", closeGroup);
		
	svg.append("text")
		.attr("class","closeText")
		.attr("x", w/2 + w/8.5 + 10)
		.attr("y", w/25 + 20)
		.text('X')
		.attr("font-family", "sans-serif")
		.style("font-size", "15px")
		.style("stroke", "white")
		.on("click", closeGroup);
    
    //***********The second part is to draw the details of the new selected PM.**********************************
    //**********Read the PM-related file to find all the circles that need to be drawn with more details*********
    var return_or_not = false;
    var pm;
    var vm_circle_radius;	

var root = ajaxPackPM(focus_node);
//console.log(root);
    
    if(root.children.length==0){
	    return_or_not = true;
        focus_node = -1;
	}
	
    pack = d3.layout.pack()
      .padding(10)
      .size([diameter/2.5, diameter/2.5])
      .value(function(d) { return d.size; });

    var node = svg.datum(root).selectAll(".node0")
      .data(pack.nodes)
      .enter().append("g")
      .attr("id",function (d){return "new "+d.name})
      .attr("class", function (d) { if (!d.parent) return "root node"; else return d.children ? "node" : "leaf node"; });
	  
	// non-clicked PMs' color
    node.append("circle")
      .attr("fill", "#CCCCCC")//"#52A55D")
      .attr("opacity", "1")
      .attr("r", function(d) { 
        return d.r; 
      })
      .attr("cx",offset)
	  .on("click", function loadPage(d){window.open(historyIP + d.name,"_blank", "toolbar=yes,menubar=yes,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);});
	  
    //select the focus gui
    svg.selectAll("g").filter(function (d){
      var id = d3.select(this).attr("id");
      if(id==currentPM)
        return true;
      else
        return false;
    }).select("circle").attr("fill",function (d){
      return "#CCCC00";//"#2C8437";
    }).on("click", function loadPage(){window.open(historyIP + focus_node,"_blank", "toolbar=yes,menubar=no,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);});

    node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

    var force = d3.layout.force()
      .charge(-diameter/10)
      .linkDistance(diameter/18)
      .size([diameter, diameter/1.5]);

var graph = ajaxLinkPM(focus_node);
//console.log(graph);

    //This part draws the links between different vm nodes		  
			old_graph = graph;

var graph2 = ajaxChangedLink();	 

			old_graph2 = graph2;

          //To do: delete lines from previous click.
          var link = svg.selectAll("link")
            .data(graph.links)
            .enter().append("line")
            .attr("class", "link")
            .attr("opacity","0")
            .style("stroke","pink")
            .style("stroke-width","2.5px")
            .attr("src",function (d){return d.source})
            .attr("dst",function (d){return d.target});
			
          var nodes = {};
		  var templink = [];
		  var templink2 = [];
		  
          graph.links.forEach(function(link) {
            link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
            link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});


		var internal_link = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link.source.name)){
              internal_link = true;
            }
          });
        if(internal_link==false){
          return;
        }
        internal_link = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link.target.name)){
              internal_link = true;
            }
          });
        if(internal_link==false){
          return;
        }
        templink.push(link);
      });
	  
          //To do: delete lines from previous click.
          var link2 = svg.selectAll("link")
            .data(graph2.links)
            .enter().append("line")
            .attr("class", "link")
            .attr("opacity","0")
            .style("stroke","pink")
            .style("stroke-width","2.5px")
            .attr("src",function (d){return d.source})
            .attr("dst",function (d){return d.target});
			
			graph2.links.forEach(function(link2) {
            link2.source = nodes[link2.source] || (nodes[link2.source] = {name: link2.source});
            link2.target = nodes[link2.target] || (nodes[link2.target] = {name: link2.target});
		  
	  
		var internal_link2 = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link2.source.name)){
              internal_link2 = true;
            }
          });
        if(internal_link2==false){
          return;
        }
        internal_link2 = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link2.target.name)){
              internal_link2 = true;
            }
          });
        if(internal_link2==false){
          return;
        }
        templink2.push(link2);
      });	  
		  
         force
          .nodes(d3.values(nodes))
         .links(templink2)
          .start();	

        force
          .nodes(d3.values(nodes))
          .links(templink)
          .start();				

		var tmp = "";
          var vm_node = svg.selectAll(".node2")
            .data(force.nodes())
            .enter().append("circle")
            .attr("class", "vm_node")
			.style("stroke", lineColorVMnode)
			.style("fill", colorVMnode)
			.style("stroke-width","1px")
            .attr("opacity","1")
            .attr("ip",function (d){return d.name})
            .attr("r", function (d){
              var result;
              tmp = d;
              d3.selectAll("g").filter(function (d){
                if(d3.select(this).attr("id")===("new "+tmp.name))
                  return true;
                else
                  return false;
              }).each(function (d){result = d3.select(this).select("circle").attr("r")});

              return result;
            })
			.on("click", function loadPage(tmp){window.open(historyIP + tmp.name,"_blank", "toolbar=yes,menubar=no,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);})
            .call(force.drag);			
			
		function colorVMnode(tmp) 
		{
			var color;
			
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == tmp.name)
				{
					if(tmp.name == focus_node)
						color = "#CCCC00";
							
					else
						color = "#CCCCCC";
				}
			}					
			return color;
		}
		
		function lineColorVMnode(tmp) 
		{
			var color;
			var flag = 0;
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == tmp.name)
				{
					if(tmp.name == focus_node)
						color = "#CCCC00";
							
					else
						color = "#CCCCCC";
				
					flag = 1;
					break;
				}
			}

			if(flag == 0)
				color = "#990000";
			
			return color;
		}		
			
	vm_node.transition().delay(timeInterval-500).duration(500).remove();	

          var path = svg.append("g").selectAll("path")
            .data(templink)
            .enter().append("path")
            .attr("class", function (d) { return d.type; })
            .attr("src", function (d) { return d.source.name; })
            .attr("dst", function (d) { return d.target.name; })
            .style("fill","none")
            .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

          svg.append("defs").selectAll("marker")
              .data(["composite","concurrent","distributed","request"])
              .enter().append("marker")
              .attr("id", function(d) { return d; })
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 5)
              .attr("refY", -0.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 4)
              .attr("orient", "auto")
              .attr("opacity", "1") 
			  .style("fill","#990000")
              .append("path")
              .attr("d", "M0,-5L10,0L0,5");
			  
		  var path2 = svg.append("g").selectAll("path")
            .data(templink2)
            .enter().append("path")
            .style("stroke",linkColor)
            .style("stroke-width",linkWidth)
			.style("stroke-dasharray",linkDash)
			.attr("opacity","0.8")
            .attr("src", function (d) { return d.source.name; })
            .attr("dst", function (d) { return d.target.name; })
            .style("fill","none")
            .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });
			
	svg.selectAll("g").transition().attr("opacity","0.9").transition().delay(timeInterval-500).duration(500).remove();	


	function linkColor(d) {
			var classType;
			
			if(d.type == "changed")
				classType = "#CCCC00";
				
			else 
				classType = "#990000";
			
			return classType;
			}		

		function linkWidth(d) {
			var classType;
			
			if(d.type == "changed")
				classType = "5px";
				
			else
				classType = "2.5px";
			
			return classType;
			}	

		function linkDash(d) {
			var classType;
			
			if(d.type == "added")
				classType = "0,0";
				
			else if(d.type == "changed")
				classType = "3,3";
				
			else
				classType = "3,3";
			
			return classType;
			}					
			
          svg.append("defs").selectAll("marker")
              .data(["composite","concurrent","distributed","request"])
              .enter().append("marker")
              .attr("id", function(d) { return d; })
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 5)
              .attr("refY", -0.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 4)
              .attr("orient", "auto")
              .attr("opacity", "1") 
			  .style("fill","#990000")
              .append("path")
              .attr("d", "M0,-5L10,0L0,5");
      
  var text_value;
          //Append ip to each of the concerned vm circle
          svg.selectAll(".vm_node").each(function (d){
            var text_x = d.x;
            var text_y = d.y;
            text_value = d3.select(this).attr("ip");
            var vm_ip = svg.append("text")
              .attr("class","vm_text")
			  .style("stroke", textColorVMnode)
              .style("text-anchor", "middle")
              .text(function(d) { 
                return text_value;
              })
              .attr("dx",function(){
                var result;
                d3.selectAll("g").filter(function (d){
                  if(d3.select(this).attr("id")===("new "+text_value))
                    return true;
                  else
                    return false;
                }).select("circle").each(function (d){
                  result = d.x + offset;
              });
                return result;
            })
              .attr("dy",function(){
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+text_value))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.y-7;
            });
            return result;
          });
		  
		vm_ip.transition().delay(timeInterval-500).duration(500).remove();	
      });

	  function textColorVMnode() 
	  {
			var color;
			
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == text_value)
				{
					color = "#990000";
				}
			}					
			return color;
		}
		
      //This part tries to locate the positions of the pm circle and vm circle in order to draw the line.
      force.on("tick", function() {
        vm_node
          .attr("cx", function(d) { 
            var vm_circle = d3.select(this).attr("ip");
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+vm_circle))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.x + offset;
            });
            return result;
          })
          .attr("cy", function(d) { 
            var vm_circle = d3.select(this).attr("ip");
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+vm_circle))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.y;
            });
            return result;
          });
        
        path.attr("d", function (d){
          var srcx=0;
          var srcy=0;
          var dstx=0;
          var dsty=0;  

          var src_circle = d3.select(this).attr("src");
		  
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcy = d.y;
          });

          var dst_circle = d3.select(this).attr("dst");
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dstx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dsty = d.y;
          });

          var dx = dstx  - srcx,
          dy = dsty - srcy,
          dr = Math.sqrt(dx * dx + dy * dy);

          return "M" + srcx + "," + srcy + "A" + dr + "," + dr + " 0 0,1 " + dstx + "," + dsty;
        });

		  path2.attr("d", function (d){
          var srcx=0;
          var srcy=0;
          var dstx=0;
          var dsty=0;		

          var src_circle = d3.select(this).attr("src");
		  
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcy = d.y;
          });

          var dst_circle = d3.select(this).attr("dst");
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dstx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dsty = d.y;
          });

		  dstx = dstx+2.5;
		  srcx = srcx+2.5;
		  dsty = dsty-2.5;
		  srcy = srcy-2.5;
		  
          var dx = dstx - srcx,
          dy = dsty - srcy,
          dr = Math.sqrt(dx * dx + dy * dy);
          return "M" + srcx + "," + srcy + "A" + dr + "," + dr + " 0 0,1 " + dstx + "," + dsty;
        });

		vm_node.attr("opacity","1");
        link.attr("opacity","0");
        d3.selectAll(".Request").attr("opacity","1");
      });	  
  });
  
  var button = svg.append("g").attr("onmouseup","setflag()");

//refresh the circles every 5 seconds by reading the data every 5 seconds
setInterval(function(){

  var changeip = ajaxChangedPM();
  deadPMs = ajaxDeadPM();
  console.log("dead");
  console.log(deadPMs);
  
  var flagUtil = false;
  
  var changedUtilDataset = ajaxChangedUtil();
//  console.log(changedUtilDataset);
  
  var changedRTDataset = ajaxChangedRT();
//  console.log(changedRTDataset);
  
  var updateCircles=[];

	for(var cntNew=0; cntNew < changedUtilDataset.length; cntNew++)
	{
		var newUtil = changedUtilDataset[cntNew];
		
		for(var cntOld=0; cntOld < dataset.length; cntOld++)
		{	
			if(dataset[cntOld].ip===newUtil.ip)
			{
				if(newUtil.cpu !== "null")
					dataset[cntOld].cpu = newUtil.cpu;
					
				if(newUtil.mem !== "null")
					dataset[cntOld].mem = newUtil.mem;

				if(newUtil.disk !== "null")
					dataset[cntOld].disk = newUtil.disk;

				if(newUtil.net_in !== "null")
					dataset[cntOld].net_in = newUtil.net_in;

				if(newUtil.net_out !== "null")
					dataset[cntOld].net_out = newUtil.net_out;	

				updateCircles.push(dataset[cntOld]);					
			}
		}
	}
	
//	console.log(updateCircles);
	
	for(var cntNew=0; cntNew < changedRTDataset.length; cntNew++)
	{
		var newUtil = changedRTDataset[cntNew];
//		console.log(newUtil.pm);
		for(var cntOld=0; cntOld < dataset.length; cntOld++)
		{	
			if(dataset[cntOld].ip===newUtil.pm)
				dataset[cntOld].rt = newUtil.rt;				
		}
	}
	  
//	console.log(dataset);
	 
	pmIPs = [];
	dataset.forEach(function (d){
          pmIPs.push(d.ip);
    });

    cpuScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return d.cpu; })])
      .range([1, 10]);

    memScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return d.mem; })])
      .range([1, 10]);

    diskScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return d.disk; })])
      .range([1, 10]);

    netinScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return d.net_in; })])
      .range([1, 10]);

    netoutScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return d.net_out; })])
      .range([1, 10]);

//x is position of the circle
    xScale = d3.scale.linear()
      .domain([0, num])
      .range([padding, w - padding*2]);
	  
   yScale = d3.scale.linear()
      .domain([0, d3.max(dataset, function(d) { return cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out); })])
      .range([diameter/6,0])


//r is the size of circle
	var rScale = d3.scale.linear()
    .domain([0, d3.max(dataset, function(d) { return cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.mem)+netinScale(d.net_in)+netoutScale(d.net_out); })])
    .range([diameter/256, diameter/25]);

	for(var i=0; updateCircles.length > i ; i++)
	{
		d3.selectAll("circle").filter(function(d){
		
		if(d3.select(this).attr("class")==="dead") // 
			return false; //
			
		else //
		{ //
			if(d3.select(this).attr("id")===updateCircles[i].ip)
				return true;
			
			else
				return false;
		} //		
		}).style("fill","#4776B4").attr("opacity","0.7").attr("class","updatedUtil");
	}
	
    svg.selectAll("circle")
    .data(dataset).filter(function (d){
      if(d3.select(this).attr("class") === "updatedUtil")
	  {	
		flagUtil = true;
		return true;
	  }
      else
        return false;
    })
   .transition().duration(1000)
    .attr("cy", function(d) {
      return h-rScale(cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out))*4;
    })
    .attr("r", function(d) {
      return  rScale(cpuScale(d.cpu)+memScale(d.mem)+diskScale(d.disk)+netinScale(d.net_in)+netoutScale(d.net_out));
    });
	
	svg.selectAll("circle").data(dataset).filter(function (d)
	{	
		if(closeGroupFlag === 1)
			return false;
			
		if(d.ip===focus_node)
		{	
			d3.selectAll(".util").remove();
			
			var currentdata = [d.ip, d.id, d.rt, d.cpu, d.mem, d.disk, d.net_in, d.net_out];
			var operation = svg.selectAll(".util").data(currentdata);

			operation.enter().append("text")
			  .attr("class","util")
			  .attr("x",function (d){
				return diameter/5.7;
			  })
			  .attr("y", function (d,i){
				return (diameter/17+(diameter/64)*i);
			  })
			  .text(function (d,i){
				
				if(i===1)
				  return "ID: "+d;
				else if(i===2)
				  return "Delay: "+d + " ms"; 	  
				else if(i===3)
				  return "CPU: "+d + " %";
				else if(i===4)
				  return "RAM: "+d + " %";
				else if(i===5)
				  return "Disk: "+d + " %";
				else if(i===6)
				  return "Inbound: "+d + " %";  
				else if(i===7)
				  return "Outbound: "+d + " %"; 
				});
		  }
	});
	
    d3.selectAll(".updatedUtil").style("fill","#4776B4").attr("opacity","0.7").attr("class","default");
	
	d3.selectAll(".change")
    .style("fill","#4776B4")
	.attr("opacity","0.7")
	.style("stroke-width","1.5px")
	.style("stroke","white")    
	.attr("class", "default");	

	for(var i=0;i<changeip.length;i++){
		
		var dTime1= 500;
		var dTime2= 300;
      d3.selectAll("circle").filter(function (d){
      
//	  if(d3.select(this).attr("class")==="dead")
//		return false;
		
//	  else
//	  {
		if(d3.select(this).attr("id")===changeip[i])	  
			return true;
      
		else
          return false;
//	  }
    })
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8");
	}	
	
    for(var i=0;i<deadPMs.length;i++){
       d3.selectAll("circle").filter(function (d){
          if(d3.select(this).attr("id")===deadPMs[i])
             return true;
          else
             return false;
       }).attr("class","dead")
         .style("fill","gray")
//		  .attr("cy", h-rScale(0))
//		 .attr("r", rScale(0))
	  .attr("opacity","0.6");
       }

      if(focus_node===-1){
      }
      else{
 
 //This means now there is a detailed circle there. We have to refresh it too.		
    d3.selectAll(".updatedUtil").style("fill","#4776B4").attr("opacity","0.7").attr("class","default");		

	root = ajaxPackPM(focus_node);
//	console.log(root);

	graph2 = ajaxChangedLink();

	var graph = ajaxLinkPM(focus_node);
//		console.log(graph);
		
	if(graph2.length == 0){
		focus_node = -1;
	}
	
	if(closeGroupFlag === 1)
		return false;	
		
			if(old_graph==graph){
				return;
			}
			
			else{
				old_graph = graph;
			}
			
			if(old_graph2==graph2){
				return;
			}
			
			else{
				old_graph2 = graph2;
			}		
				
	var close = svg.append("rect")
		.attr("class","closeButton")
		.attr("x", w/2 + w/8.5)
		.attr("y", w/25)
		.attr("width", 30)
		.attr("height", 30)
		.style("stroke", "white")
		.style("fill", "#990000")
		.on("click", closeGroup);
		
	svg.append("text")
		.attr("class","closeText")
		.attr("x", w/2 + w/8.5 + 10)
		.attr("y", w/25 + 20)
		.text('X')
		.attr("font-family", "sans-serif")
		.style("font-size", "15px")
		.style("stroke", "white")
		.on("click", closeGroup);
		
        d3.selectAll("circle")
        .filter(function (d){
          var currentclass = d3.select(this).attr("class");
          if(currentclass === "vm_node")
            return true;
          else
            return false;
        })
        .remove();

        d3.selectAll("line").remove();

        d3.selectAll("path").remove();
		
		d3.selectAll("node0").remove();
		d3.selectAll("node2").remove();

        d3.selectAll("text").filter(function (d){
          var currentclass = d3.select(this).attr("class");
          if((currentclass==="closeText") || (currentclass==="util"))
            return false;
			
          else
            return true;
        })
        .remove();

        d3.selectAll("marker").remove();

        d3.selectAll("g").filter(function (d){
          var currentclass = d3.select(this).attr("class");
          if((currentclass === "node")||(currentclass ==="leaf node")||(currentclass ==="root node"))
            return true;
          else
            return false;
        })
        .remove();

        //Draw the new detailed part.
        //**********Read the PM-related file to find all the circles that need to be drawn with more details*********
        var currentPM = "new "+focus_node;
        var return_or_not = false;
        var pm;
        var vm_circle_radius;

        pack = d3.layout.pack()
          .padding(10)
          .size([diameter/2.5, diameter/2.5])
          .value(function(d) { return d.size; });

        var node = svg.datum(root).selectAll(".node0")
          .data(pack.nodes)
          .enter().append("g")
          .attr("id",function (d){return "new "+d.name})
          .attr("class", function (d) { if (!d.parent) return "root node"; else return d.children ? "node" : "leaf node"; });

        node.append("circle")
          .attr("fill-opacity", "0.9")
          .attr("stroke-width", "2px")
          .attr("r", function(d) { 
            return d.r; 
        })
        .attr("cx",offset)
		.on("click", function loadPage(d){window.open(historyIP + d.name,"_blank", "toolbar=no,menubar=no,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);});  
		
        //select the focus gui
        svg.selectAll("g").filter(function (d){
          var id = d3.select(this).attr("id");
          if(id==currentPM)
            return true;
          else
            return false;
        }).select("circle").attr("fill",function (d){
          return "#CCCC00";//"#2C8437";
        })
		.on("click", function loadPage(){window.open(historyIP + focus_node,"_blank", "toolbar=yes,menubar=no,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);});
		
        node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; }).transition().duration(500).attr("opacity","0.9");		

        var force = d3.layout.force()
          .charge(-diameter/10)
          .linkDistance(diameter/18)
          .size([diameter, diameter/1.5]);
		  
        //To do: delete lines from previous click.
          var link = svg.selectAll("link")
            .data(graph.links)
            .enter().append("line")
            .attr("class", "link")
            .attr("opacity","0")
            .style("stroke","pink")
            .style("stroke-width","2.5px")
            .attr("src",function (d){return d.source})
            .attr("dst",function (d){return d.target});	
			
          var nodes = {};
		  var templink = [];
		  var templink2 = [];
		  
          graph.links.forEach(function(link) {
            link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
            link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});

		var internal_link = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link.source.name)){
              internal_link = true;
            }
          });
        if(internal_link==false){
          return;
        }
        internal_link = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link.target.name)){
              internal_link = true;
            }
          });
        if(internal_link==false){
          return;
        }
        templink.push(link);
      });
	  
          //To do: delete lines from previous click.
          var link2 = svg.selectAll("link")
            .data(graph2.links)
            .enter().append("line")
            .attr("class", "link")
            .attr("opacity","0")
            .style("stroke","pink")
            .style("stroke-width","2.5px")
            .attr("src",function (d){return d.source})
            .attr("dst",function (d){return d.target});	
			
			graph2.links.forEach(function(link2) {
            link2.source = nodes[link2.source] || (nodes[link2.source] = {name: link2.source});
            link2.target = nodes[link2.target] || (nodes[link2.target] = {name: link2.target});
		  
	  
		var internal_link2 = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link2.source.name)){
              internal_link2 = true;
            }
          });
        if(internal_link2==false){
          return;
        }
        internal_link2 = false;
        d3.selectAll("g").each(function (d){
            if(d3.select(this).attr("id")===("new "+link2.target.name)){
              internal_link2 = true;
            }
          });
        if(internal_link2==false){
          return;
        }
        templink2.push(link2);
      });	  
		  
          force
            .nodes(d3.values(nodes))
           .links(templink2)
            .start();	

          force
            .nodes(d3.values(nodes))
            .links(templink)
            .start();				

		  var tmp = "";
          var vm_node = svg.selectAll(".node2")
            .data(force.nodes())
            .enter().append("circle")
            .attr("class", "vm_node")
			.style("stroke", lineColorVMnode)
			.style("fill", colorVMnode)
			.style("stroke-width","1px")
            .attr("opacity","1")
            .attr("ip",function (d){return d.name})
            .attr("r", function (d){
              var result;
              tmp = d;
              d3.selectAll("g").filter(function (d){
                if(d3.select(this).attr("id")===("new "+tmp.name))
                  return true;
                else
                  return false;
              }).each(function (d){result = d3.select(this).select("circle").attr("r")});

				return result;
            })
			.on("click", function loadPage(tmp){window.open(historyIP + tmp.name,"_blank", "toolbar=yes,menubar=no,resizable=yes,scrollbars=yes,width=" + pageWidth/1.2 + ",height=" + pageHeight/1.2);})
            .call(force.drag);

		function colorVMnode(tmp) 
		{
			var color;
			
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == tmp.name)
				{
					if(tmp.name == focus_node)
						color = "#CCCC00";
							
					else
						color = "#CCCCCC";
				}
			}					
			return color;
		}
		
		function lineColorVMnode(tmp) 
		{
			var color;
			var flag = 0;
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == tmp.name)
				{
					if(tmp.name == focus_node)
						color = "#CCCC00";
							
					else
						color = "#CCCCCC";
				
					flag = 1;
					break;
				}
			}

			if(flag == 0)
				color = "#990000";
			
			return color;
		}

          var path = svg.append("g").selectAll("path")
            .data(templink)
            .enter().append("path")
            .attr("class", function (d) { return d.type; })
            .attr("src", function (d) { return d.source.name; })
            .attr("dst", function (d) { return d.target.name; })
            .style("fill","none")
            .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

          svg.append("defs").selectAll("marker")
              .data(["composite","concurrent","distributed","request"])
              .enter().append("marker")
              .attr("id", function(d) { return d; })
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 5)
              .attr("refY", -0.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 4)
              .attr("orient", "auto")
              .attr("opacity", "1") 
			  .style("fill","#990000")
              .append("path")
              .attr("d", "M0,-5L10,0L0,5");
			  
		  var path2 = svg.append("g").selectAll("path")
            .data(templink2)
            .enter().append("path")
            .style("stroke",linkColor)
            .style("stroke-width",linkWidth)
			.style("stroke-dasharray",linkDash)
	        .attr("opacity","0.8")
            .attr("src", function (d) { return d.source.name; })
            .attr("dst", function (d) { return d.target.name; })
            .style("fill","none")
            .attr("marker-end", function(d) { return "url(#" + d.type + ")"; });

	if(graph2.length>0)
		svg.selectAll("g").transition().attr("opacity","0.1").transition().duration(500).attr("opacity","0.9").transition().delay(timeInterval-500).remove();			

	function linkColor(d) {
			var classType;
			
			if(d.type == "changed")
				classType = "#CCCC00";
				
			else 
				classType = "#990000";
			
			return classType;
			}		

		function linkWidth(d) {
			var classType;
			
			if(d.type == "changed")
				classType = "5px";
				
			else
				classType = "2.5px";
			
			return classType;
			}	

		function linkDash(d) {
			var classType;
			
			if(d.type == "added")
				classType = "0,0";
				
			else if(d.type == "changed")
				classType = "3,3";
				
			else
				classType = "3,3";
			
			return classType;
			}			
			
          svg.append("defs").selectAll("marker")
              .data(["composite","concurrent","distributed","request"])
              .enter().append("marker")
              .attr("id", function(d) { return d; })
              .attr("viewBox", "0 -5 10 10")
              .attr("refX", 5)
              .attr("refY", -0.5)
              .attr("markerWidth", 6)
              .attr("markerHeight", 4)
              .attr("orient", "auto")
              .attr("opacity", "1") 
			  .style("fill","#990000")
              .append("path")
              .attr("d", "M0,-5L10,0L0,5");
			  
	for(var i=0;i<changeip.length;i++){		
		var dTime1= 500;
		var dTime2= 300;
      d3.selectAll("circle").filter(function (d){
      
//	  if(d3.select(this).attr("class")==="dead")
//		return false;
		
//	  else
//	  {
		if(d3.select(this).attr("id")===changeip[i])	  
			return true;
      
		else
          return false;
//	  }
    })
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8")
	.transition().duration(dTime2).attr("class","change").style("fill","white").style("stroke","#C32F4B")
	.transition().duration(dTime1).attr("class","change").style("fill","#C32F4B").style("stroke","white").attr("opacity","0.8");
	}			        

			var text_value;
    //Append ip to each of the concerned vm circle
          svg.selectAll(".vm_node").each(function (d){
            var text_x = d.x;
            var text_y = d.y;
            text_value = d3.select(this).attr("ip");
            var vm_ip = svg.append("text")
              .attr("class","vm_text")
			  .style("stroke",textColorVMnode)
              .style("text-anchor", "middle")
              .text(function(d) { 
                return text_value;
              })
              .attr("dx",function(){
                var result;
                d3.selectAll("g").filter(function (d){
                  if(d3.select(this).attr("id")===("new "+text_value))
                    return true;
                  else
                    return false;
                }).select("circle").each(function (d){
                  result = d.x + offset;
              });
                return result;
            })
              .attr("dy",function(){
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+text_value))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.y-7;
            });
            return result;
          });
      });
	  
	  function textColorVMnode() 
	  {
			var color;
			
		    for(var i=0;i<pmIPs.length;i++){
				if(pmIPs[i] == text_value)
				{
					color = "#990000";
				}
			}					
			return color;
		}

      //This part tries to locate the positions of the pm circle and vm circle in order to draw the line.
      force.on("tick", function() {
      vm_node
          .attr("cx", function(d) { 
            var vm_circle = d3.select(this).attr("ip");
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+vm_circle))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.x + offset;
            });
            return result;
          })
          .attr("cy", function(d) { 
            var vm_circle = d3.select(this).attr("ip");
            var result;
            d3.selectAll("g").filter(function (d){
              if(d3.select(this).attr("id")===("new "+vm_circle))
                return true;
              else
                return false;
            }).select("circle").each(function (d){
              result = d.y;
            });
            return result;
          });			  		  
		  
        path.attr("d", function (d){		
		
          var srcx=0;
          var srcy=0;
          var dstx=0;
          var dsty=0;  
		  
          var src_circle = d3.select(this).attr("src");
         
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcy = d.y;
          });

          var dst_circle = d3.select(this).attr("dst");
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dstx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dsty = d.y;
          });
		  
          var dx = dstx  - srcx,
          dy = dsty - srcy,
          dr = Math.sqrt(dx * dx + dy * dy);

          return "M" + srcx + "," + srcy + "A" + dr + "," + dr + " 0 0,1 " + dstx + "," + dsty;
});	
		path2.attr("d", function (d){
			
          var srcx=0;
          var srcy=0;
          var dstx=0;
          var dsty=0;		

          var src_circle = d3.select(this).attr("src");

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+src_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            srcy = d.y;
          });

          var dst_circle = d3.select(this).attr("dst");
          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dstx = d.x + offset;
          });

          d3.selectAll("g").filter(function (d){
            if(d3.select(this).attr("id")===("new "+dst_circle))
              return true;
            else
              return false;
          }).select("circle").each(function (d){
            dsty = d.y;
          });

		  dstx = dstx+2.5;
		  srcx = srcx+2.5;
		  dsty = dsty-2.5;
		  srcy = srcy-2.5;
		  
          var dx = dstx - srcx,
          dy = dsty - srcy,
          dr = Math.sqrt(dx * dx + dy * dy);

          return "M" + srcx + "," + srcy + "A" + dr + "," + dr + " 0 0,1 " + dstx + "," + dsty;
 });
		
        vm_node.attr("opacity","1");
        link.attr("opacity","0");
        d3.selectAll(".Request").attr("opacity","1");   

        force
            .nodes(d3.values(nodes))
           .links(templink2)
            .stop();	

        force
            .nodes(d3.values(nodes))
            .links(templink)
            .stop();						
      });
   }
},timeInterval)

</script>
</body>
</html>

