$(document).ready(function() {
    loadStations();

    $('#loadBtn').click(function() {
        const stationId = $('#stationSelect').val();
        if(stationId) {
            loadMeasurements(stationId);
        }
    });

    function loadStations() {
        $.ajax({
            url: '/api/v1/stations',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                const select = $('#stationSelect');
                select.empty();
                data.forEach(station => {
                    select.append(`<option value="${station.id}">${station.stationName} (${station.id})</option>`);
                });

                if(data.length > 0) loadMeasurements(data[0].id);
            },
            error: function(xhr) {
                showError("Fehler beim Laden der Stationen.");
            }
        });
    }

    function loadMeasurements(stationId) {
        $('#loading').show();
        $('#error').hide();

        const format = $('#formatSelect').val();
        const acceptHeader = format === 'json' ? 'application/json' : 'application/xml';

        $.ajax({
            url: `/api/v1/stations/${stationId}/measurements/current`,
            headers: { 'Accept': acceptHeader },
            dataType: 'text', // Wichtig für XML-Parsing
            success: function(data) {
                if(format === 'json') {
                    renderCurrent(JSON.parse(data));
                } else {
                    const xml = $.parseXML(data);
                    const measurement = parseXmlMeasurement(xml);
                    renderCurrent(measurement);
                }
            }
        });

        // 2. Historie laden (mit Filter)
        $.ajax({
            url: `/api/v1/stations/${stationId}/measurements?limit=10`,
            headers: { 'Accept': acceptHeader },
            dataType: 'text',
            success: function(data) {
                $('#loading').hide();
                const tbody = $('#historyTable tbody');
                tbody.empty();

                let measurements = [];
                if(format === 'json') {
                    measurements = JSON.parse(data);
                } else {
                    const xml = $.parseXML(data);
                    $(xml).find('item, measurement').each(function() {
                        measurements.push(parseXmlMeasurement(this));
                    });
                }

                measurements.forEach(m => {

                    tbody.append(`
                        <tr class="${m.warningLevel}">
                            <td>${stationId}</td>
                            <td>${m.timestamp}</td>
                            <td>${m.waterLevel.toFixed(2)}</td>
                            <td>${m.flowRate.toFixed(2)}</td>
                            <td>${m.precipitation.toFixed(2)}</td>
                            <td>${m.warningLevel}</td>
                        </tr>
                    `);
                });
            },
            error: function(xhr) {
                $('#loading').hide();
                showError("Fehler beim Laden der Historie.");
            }
        });
    }

    function parseXmlMeasurement(xmlNode) {

        const $node = $(xmlNode);
        return {
            timestamp: $node.find('timestamp').text(),
            waterLevel: parseFloat($node.find('waterLevel').text()),
            flowRate: parseFloat($node.find('flowRate').text()),
            precipitation: parseFloat($node.find('precipitation').text()),
            warningLevel: $node.find('warningLevel').text()
        };
    }

    function renderCurrent(m) {
        $('#currentMeasurement').html(`
            <strong>Zeitpunkt:</strong> ${m.timestamp} <br>
            <strong>Wasserstand:</strong> ${m.waterLevel.toFixed(2)} cm <br>
            <strong>Durchfluss:</strong> ${m.flowRate.toFixed(2)} m³/s <br>
            <strong>Warnstufe:</strong> <span class="${m.warningLevel}">${m.warningLevel}</span>
        `);
    }

    function showError(msg) {
        $('#error').text(msg).show();
    }
});