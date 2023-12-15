// Please see documentation at https://learn.microsoft.com/aspnet/core/client-side/bundling-and-minification
// for details on configuring this project to bundle and minify static web assets.

// Write your JavaScript code.
window.onload = function () {
    // Temperature pill
    var cpuTemperature = parseFloat(document.getElementById('tempGraphText').innerText);
    var maxTemperature = 100;
    var tempMaskWidth = 100 - (cpuTemperature / maxTemperature) * 100;
    var tempMask = document.getElementById('tempGraphMask');
    tempMask.style.width = tempMaskWidth + '%';

    // CPU usage pill
    var cpuUsage = parseFloat(document.getElementById('cpuUsageGraphText').innerText);
    var cpuUsageMask = document.getElementById('cpuUsageGraphMask');
    cpuUsageMask.style.width = (100 - cpuUsage) + '%';

    // RAM usage pill
    var ramUsage = parseFloat(document.getElementById('ramUsageGraphText').innerText);
    var ramUsageMask = document.getElementById('ramUsageGraphMask');
    ramUsageMask.style.width = (100 - ramUsage) + '%';

    // Storage usage pills
    var storageElements = document.querySelectorAll('[id^="storageUsageGraphText_"]');
    storageElements.forEach(function (el) {
        var storageUsage = parseFloat(el.innerText);
        var storageIndex = el.id.split('_')[1];
        var storageMask = document.getElementById('storageUsageGraphMask_' + storageIndex);
        storageMask.style.width = (100 - storageUsage) + '%';
    });
};