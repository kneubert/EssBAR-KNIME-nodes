var body = document.getElementsByTagName('body')[0];
var html = "<h1>Data available?</h1>";
if (knimeDataTable) {
    html += '<div class="success">Data available. Node correctly configured. Table contains ' + JSON.stringify(knimeDataTable.getRows()[0].data) + ' rows.</div>';
} else {
    html += '<div class="failure">No data available.</div>';
}
document.open();
document.write(knimeDataTable.getRows()[0].data);
document.close();
