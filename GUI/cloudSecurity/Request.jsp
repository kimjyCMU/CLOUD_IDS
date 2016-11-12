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


.axis path, .axis line {
  fill: none;
  stroke-width: .1px;
}

</style>

</head>

<body style="background:white;">
	
<form id="dataForm" >
	<input type="hidden" id="system" value=""/>
	<input type="hidden" id="network" value=""/>
	<input type="hidden" id="request" value=""/>
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
	document.getElementById("request").value=indexIP;     
	
	var msgSystem=$('#system').val(); 
	var msgNetwork=$('#network').val();
	var msgRequest=$('#request').val();

	$.ajax({
		'async': false,
		'url':'ActionServlet',
		'dataType':"json",
		'data':{system:msgSystem,network:msgNetwork,request:msgRequest},
		'success':function(responseText){
			ajaxResult=responseText;
		}	
	});
	document.getElementById("request").value=null;      
	return ajaxResult;
}


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
	
var WIDTH = pageWidth/1.2, 
	HEIGHT = pageHeight/1.3, 
	MARGINS = {top: 20, right: 100, bottom: 30, left: 50};

var indexIP=<%=ip%>;
console.log("ip" + indexIP);

var data = ajaxdata(indexIP);
var neighborAddr = [];
var neighNum = 0;

data.forEach(function(d){
	d.TS = new Date(+d.TS*1000);	
	
	if(neighborAddr.indexOf(d.Addr) < 0)
		neighborAddr.push(d.Addr);
	
	neighNum = neighborAddr.length;
});	

console.log(data);
console.log(neighborAddr);
console.log(neighNum);

// ### Edit this array for your preference (out of "req", "res","ratio") ### //
var metricType = ["req", "res","ratio"]; 
var metrics = [];
var metricGroup;

// for a specific neighbor
var neighbors = new Array();
var neighborGroups = new Array();

for(var i=0; i < neighNum; i++)
{
	neighbors[i] = new Array();
	neighborGroups[i] = new Array();
}

for(var i=0; i < data.length; i++)
{
	for(var k=0; k < neighNum; k++)
	{
		if(data[i].Addr == neighborAddr[k])
		{
			data[i].Type = data[i].Addr + "/" + data[i].Type;
			neighbors[k].push(data[i]);
			break;
		}
	}
}

for(var i=0; i < data.length; i++)
{
	for(var k=0; k < metricType.length; k++)
	{
		if(data[i].Type.includes(metricType[k]))
		{
			metrics.push(data[i]);
			break;
		}
	}
}

// a specific metric or all the metrics
metricGroup = d3.nest()
      .key(function(d) {return d.Type;})
      .entries(metrics);

// a specific neighbor
for(var k=0; k < neighNum; k++)
{
	neighborGroups[k] = d3.nest()
      .key(function(d) {return d.Type;})
      .entries(neighbors[k]);
}

var currentTime = new Date();

var	xScale = d3.time.scale().range([MARGINS.left, WIDTH - MARGINS.right]).domain([d3.min(data, function(d) {return d.TS;}), currentTime]);
var yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0, d3.max(data, function(d) {return d.Value;})]);

var xAxis = d3.svg.axis().scale(xScale).tickSize(-HEIGHT).orient("bottom").tickSubdivide(true).tickPadding(10);
var yAxis = d3.svg.axis().scale(yScale).tickSize(-WIDTH).orient("left").tickSubdivide(true).tickPadding(10);  

var color = d3.scale.category10();
	
// Draw lines
var lineGen = d3.svg.line()
	.x(function(d) {return xScale(d.TS);})
    .y(function(d) {return yScale(d.Value);})
    .interpolate("basis");

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
	.attr("y", MARGINS.left-50)
	.attr("x", -HEIGHT/2)
	.text('#req / # res');
		
	svg.append("clipPath")
		.attr("id", "clip")
		.append("rect")
		.attr("x",MARGINS.left)
		.attr("width", WIDTH - MARGINS.right)
		.attr("height",HEIGHT - MARGINS.top);


var dataType = [];

// ##### Choose metrics or neighbors[x] #### //
metrics.forEach(function(d){ 
	
	if(dataType.indexOf(d.Type) < 0)
		dataType.push(d.Type);
});	

dataType.forEach(function(d,i) {		   
   svg.append('rect')
   		.attr("x", WIDTH - (MARGINS.right/1.5+30))
		.attr("y", MARGINS.top + (i * 20))
		.attr('width', 10)
		.attr('height', 10)
		.style('fill', color(i));
		
   svg.append('text')
		.attr('x', WIDTH - (MARGINS.right/1.5))
		.attr('y', MARGINS.top + (i * 20 +10))
		.text(d);
	});
	svg.call(d3.behavior.zoom().x(xScale).on("zoom", zoom));
	draw();	
	
function draw() {
  d3.selectAll("path").remove();
  
  svg.select("g.x.axis").call(xAxis);
  svg.select("g.y.axis").call(yAxis); 

// ##### Choose metricGroup or neighborGroups[x] #### //
  metricGroup.forEach(function(d,i) { 
	svg.append("path")
		.attr('d', lineGen(d.values))
		.attr("clip-path","url(#clip)")
		.attr('stroke', color(i))
		.attr("cursor","move")
		.attr("pointer-events","all")
		.attr('fill', 'none');
	});

}

function zoom() {
  draw();
}	
</script>
</body>
</div>
</div>

</html>

