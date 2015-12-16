$(document).ready(function() {
    var websocket = null,
        charts = [],
        cores = [];

    $("#updateButton").on("click", function() {
        cores.length = 0;

        $(".coreCheckBoxes").each(function(index, element) {
            var $element = $(element);

    	    if ($element.is(':checked') === true) {
                cores.push($element.attr('value'));
    	    }
	    });

        if (websocket !== null) {
            websocket.close();
        }

        updateChartDisplay(charts, cores);

        websocket = new WebSocket("ws://localhost:8080/websocket-test/cpu/" + cores.join());
        websocket.onmessage = function (event) {
            var dataJson = JSON.parse(event.data),
                timestamp = dataJson["timestamp"],
                cpuStatsCollection = dataJson["cpuStats"],
                sys = null,
                usr = null,
                ioWait = null;

            for (var i = 0; i < cpuStatsCollection.length; i++) {
                var cpuStatsItem = cpuStatsCollection[i],
                    chartObject = charts[cpuStatsItem["cpuId"]];

                sys = cpuStatsItem["userTime"];
                usr = cpuStatsItem["systemTime"];
                ioWait = cpuStatsItem["ioWaitTime"];



                chartObject.addData([sys, usr, ioWait], formatEpochMillisAsDate(timestamp));
                chartObject.update();
            }
        }
    });

    updateCoreList(charts);

});

function updateCoreList(cores, charts) {
    $.get("./api/cores", function(data) {
    	var $coreList = $("#coreList"),
            $chartsDiv = $("#charts")
            ;

        cores.length = 0;

        $coreList.empty();
        $chartsDiv.empty();

        $coreList.html("");

        for (var coreIndex = 0; coreIndex < data.length; coreIndex++) {
            cores.push(data[coreIndex]);
            $coreList.append(
                '<input id="cpuCoreCheck-' + data[coreIndex] + '" ' +
                '       value="' + data[coreIndex] + '" ' +
                '       class="coreCheckBoxes" ' +
                '       type="checkbox">' + data[coreIndex] + '</input>');
        }
    });
}

function updateChartDisplay(charts, chartIds) {
    var $chartsDiv = $("#charts");

    $chartsDiv.empty();
    charts.length = 0;

    for (var coreIndex = 0; coreIndex < chartIds.length; coreIndex++) {
        var canvasContext = null;

        $chartsDiv.append(
            '<div id="cpuUsageChartDiv-' + chartIds[coreIndex] + '" class="chart">' +
            '    <h3>Core: ' + chartIds[coreIndex] + '</h3>' +
            '    <canvas id="cpuUsageChart-' + chartIds[coreIndex] + '"></canvas>' +
            '</div>');

        canvasContext = document.getElementById("cpuUsageChart-" + chartIds[coreIndex]).getContext("2d");
        charts[chartIds[coreIndex]] = new Chart(canvasContext).Line(getNewDataObject(), {});
    }

    $chartsDiv.append(
        '<div id="legend" class="chart legend">' +
        '    <h3>Legend</h3>' +
        '    <ul>' +
        '        <li class="sys">Sys</li>' +
        '        <li class="usr">Usr</li>' +
        '        <li class="iowait">IoWait</li>' +
        '    </ul>' +
        '</div>');
}

function getNewDataObject() {
    var data = {
        labels: [],
        datasets: [
            {
                label: "Sys",
                fillColor: "rgba(60,255,51,0.2)",
                strokeColor: "rgba(60,255,51,1)",
                pointColor: "rgba(60,255,51,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(60,255,51,1)",
                data: []
            },
            {
                label: "Usr",
                fillColor: "rgba(151,187,205,0.2)",
                strokeColor: "rgba(151,187,205,1)",
                pointColor: "rgba(151,187,205,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(151,187,205,1)",
                data: []
            },
            {
                label: "IoWait",
                fillColor: "rgba(200,87,5,0.2)",
                strokeColor: "rgba(200,87,5,1)",
                pointColor: "rgba(200,87,5,1)",
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: "rgba(200,87,5,1)",
                data: []
            }

        ]
    };

    return data;
}

function formatEpochMillisAsDate(millis) {
    return moment(millis, "x").format("HH:mm:ss");
}