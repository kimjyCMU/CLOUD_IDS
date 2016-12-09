<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*,java.lang.*,servlet.*,main.*,configuration.*" %>
<%@ page import="com.mongodb.*" %>
<%@ page import="json.simple.*" %>
<%@ page import="gson.*" %>

<html lang="en">

<head>
    <link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://getbootstrap.com/examples/justified-nav/justified-nav.css" rel="stylesheet">

<style>	

.axis path,
.axis line {
    fill: none;
    stroke: grey;
    stroke-width: 1;
    shape-rendering: crispEdges;
}


</style>

</head>

<body style="background:white;">
	
<form id="dataForm" >
	<input type="hidden" id="system" value=""/>
	<input type="hidden" id="network" value=""/>
	<input type="hidden" id="request" value=""/>
	<input type="hidden" id="UA" value=""/>
	<input type="hidden" id="allUAs" value=""/>
</form>

<script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
    <link type="text/css" rel="stylesheet" href="http://mbostock.github.io/d3/talk/20111018/style.css"/>


<script>

<%	
	webMain web = new webMain();
	
	String ip = request.getParameter("ip");
	ip = "\"" + ip + "\"" ;
%>

function ajaxdata(indexIP){
	var ajaxResult="";     
	document.getElementById("allUAs").value=indexIP;     
	
	var msgSystem=$('#system').val(); 
	var msgNetwork=$('#network').val();
	var msgRequest=$('#request').val();
	var msgUA=$('#UA').val();
	var msgAllUAs=$('#allUAs').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{system:msgSystem,network:msgNetwork,request:msgRequest,UA:msgUA,allUAs:msgAllUAs},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("allUAs").value=null;      
	return ajaxResult;
}


var pageWidth = window.innerWidth,
    pageHeight = window.innerHeight;
    if (typeof pageWidth != "number"){
        if (document.compatMode == "CSS1Compat"){
            pageWidth = document.documentElement.clientWidth;
            pageHeight = document.documentElement.clientHeight;
        } 
		else {
            pageWidth = document.body.clientWidth;
            pageHeight = document.body.clientHeight;
        }		
	}
	
var WIDTH = pageWidth/1.2, 
	HEIGHT = pageHeight/1.3, 
	MARGINS = {top: 20, right: 100, bottom: 30, left: 50};

var indexIP=<%=ip%>;
console.log(indexIP);

var data = ajaxdata(indexIP);

var minTS = data.slice(0).sort(function(a, b) { return a.TS - b.TS})[0].TS;
console.log(minTS); 

var color = d3.scale.category10();


var valUA = ["normal", "flooding", "APIabuse", "spoofing", "bruteforce","ProtocolManipulation","analysis","injection"];
console.log(valUA);

var IPs = [];

data.forEach(function(d){
	d.TS = d.TS - minTS; 
	
	if(IPs.indexOf(d.IP) < 0)
		IPs.push(d.IP);
});	

data = data.sort(function(a,b) {return (a.TS > b.TS) ? 1 : ((b.TS > a.TS) ? -1 : 0);} ); 
console.log(data);

var dataGroup = d3.nest()
      .key(function(d) {return d.IP;})
      .entries(data);

console.log(JSON.stringify(dataGroup));

var currentTime = new Date();

var	xScale = d3.scale.ordinal().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(data, function(d) {return d.TS;}), d3.max(data, function(d) {return d.TS;})]);
var yScale = d3.scale.ordinal().rangePoints([HEIGHT - MARGINS.top, MARGINS.bottom]).domain(valUA);

var xAxis = d3.svg.axis().scale(xScale).tickSize(-HEIGHT).orient("bottom").tickSubdivide(true).tickPadding(10);
var yAxis = d3.svg.axis().scale(yScale).tickSize(-WIDTH).orient("left").tickSubdivide(true).tickPadding(10);

var lSpace = WIDTH/dataGroup.length;  
	
// Draw lines
var lineGen = d3.svg.line()
	.x(function(d) {return xScale(d.TS);})
    .y(function(d) {return yScale(d.Value);})
;

// Draw x axis and y axis
var svg = d3.select("body").append("svg")
	.attr("width", WIDTH + MARGINS.left + MARGINS.right)
    .attr("height", HEIGHT + MARGINS.top + MARGINS.bottom)
	.append("g")
		.attr("transform", "translate(" + MARGINS.left + "," + MARGINS.top + ")");
          
	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0," + HEIGHT + ")")
		.call(xAxis);
		
	svg.append("g")
		.attr("class", "y axis")
		.attr("transform", "translate(" + MARGINS.left + ",0)")
		.call(yAxis);
		
	svg.append("g")
	.attr("class", "y axis")
	.append("text")
	.attr("class", "axis-label")
	.attr("transform", "rotate(-90)")
	.attr("y", MARGINS.left-80)
	.attr("x", -HEIGHT/2)
	.text('Unit actions');
		
	svg.append("clipPath")
		.attr("id", "clip")
		.append("rect")
		.attr("x",MARGINS.left)
		.attr("width", WIDTH - MARGINS.right)
		.attr("height",HEIGHT - MARGINS.top);

dataGroup.forEach(function(d,i) {		   
   svg.append('rect')
   		.attr("x", WIDTH - (MARGINS.right/2))
		.attr("y", MARGINS.top + (i * 20))
		.attr('width', 10)
		.attr('height', 10)
		.style('fill', color(i));
		
   svg.append('text')
		.attr('x', WIDTH - (MARGINS.right/2) + 30)
		.attr('y', MARGINS.top + (i * 20 +10))
		.text(d.key);
	});

	svg.call(d3.behavior.zoom().x(xScale).on("zoom", zoom));
	draw();	
	
function draw() {
  d3.selectAll("path").remove();
  d3.selectAll("circle").remove();
  
  svg.select("g.x.axis").call(xAxis);
  svg.select("g.y.axis").call(yAxis); 

  dataGroup.forEach(function(d,i) {
	svg.append("path")
		.attr('d', lineGen(d.values))
		.attr("clip-path","url(#clip)")
		.attr('stroke', color(i))
		.attr("cursor","move")
		.attr("pointer-events","all")
        .attr('fill', 'none');
	});
	
	data.forEach(function(d,i) {
	// Add the valueline path.
	svg.selectAll("dot")
        .data(data)
      .enter().append("circle")
		.attr("cursor","move")
		.attr("cx", xScale(d.TS))
		.attr("cy", yScale(d.Value))
		.style("stroke-width","1px")
		.style("stroke", getColor(d.IP))
		.style("fill", "none")
		.attr("r", getRadius(d.IP));	
	});
}

function getColor(ip)
{
	var result;
	
	for(var i=0; i < IPs.length; i++)
	{
		if(ip == IPs[i])
			result = color(i);
	}
		
	return result;
}

function getRadius(value)
{
	var result;
	
	for(var i=0; i < IPs.length; i++)
	{
		if(value === IPs[i])
			result = i+2;
	}
	
	return result;
}

function zoom() {
  draw();
}	
</script>
</body>
</div>
</div>

</html>

