google.charts.load('current', {packages: ['gantt']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
    fetch('/api/reservas/equipamentos/{equipamentoId}')
        .then(response => response.json())
        .then(data => {
            const dataTable = new google.visualization.DataTable();
            dataTable.addColumn('string', 'ID');
            dataTable.addColumn('string', 'Tarefa');
            dataTable.addColumn('string', 'Recurso');
            dataTable.addColumn('date', 'Início');
            dataTable.addColumn('date', 'Fim');
            dataTable.addColumn('number', 'Duração');
            dataTable.addColumn('number', 'Percentual Completo');
            dataTable.addColumn('string', 'Dependências');

            data.forEach(item => {
                dataTable.addRow([
                    item.id.toString(),
                    `Reserva de ${item.equipamento.nome}`,
                    item.cliente.nome,
                    new Date(item.dataInicio),
                    new Date(item.dataFim),
                    null,
                    0,
                    null
                ]);
            });

            const options = {
                height: 400,
                gantt: {
                    trackHeight: 30
                }
            };

            const chart = new google.visualization.Gantt(document.getElementById('ganttChart'));
            chart.draw(dataTable, options);
        })
        .catch(error => console.error('Erro ao carregar dados:', error));
}