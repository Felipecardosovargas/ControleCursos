import { buscarDadosEngajamento } from './relatorio.js';

window.addEventListener('DOMContentLoaded', async () => {
    const data = await buscarDadosEngajamento();
    if (!data) return;

    const cursos = data.map(item => item.cursoNome); // ✅ Defina aqui!

    // Gráfico de Matrículas
    new Chart(document.getElementById("chartMatriculas"), {
        type: "bar",
        data: {
            labels: cursos,
            datasets: [{
                label: "Matrículas",
                data: data.map(item => item.totalAlunosMatriculados),
                backgroundColor: "#4e79a7"
            }]
        }
    });

    // Gráfico de Média de Idade
    new Chart(document.getElementById("chartMediaIdade"), {
        type: "bar",
        data: {
            labels: cursos,
            datasets: [{
                label: "Média de Idade",
                data: data.map(item => item.mediaIdadeAlunos),
                backgroundColor: "#f28e2b"
            }]
        }
    });

    // Gráfico de Novos Alunos
    new Chart(document.getElementById("chartNovosAlunos"), {
        type: "bar",
        data: {
            labels: cursos,
            datasets: [{
                label: "Novos Alunos (30 dias)",
                data: data.map(item => item.novosAlunosUltimos30Dias),
                backgroundColor: "#59a14f"
            }]
        }
    });

    // Gráfico de Linha: Novos Alunos por Curso
    new Chart(document.getElementById("chartLinhaNovosAlunos"), {
        type: "line",
        data: {
            labels: cursos,
            datasets: [{
                label: "Novos Alunos (30 dias)",
                data: data.map(item => item.novosAlunosUltimos30Dias),
                fill: false,
                borderColor: "#e15759",
                tension: 0.1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                title: { display: true, text: 'Evolução de Novos Alunos' }
            }
        }
    });

// Gráfico de Radar: Comparativo de Indicadores
    new Chart(document.getElementById("chartRadarComparativo"), {
        type: "radar",
        data: {
            labels: cursos,
            datasets: [
                {
                    label: "Matrículas",
                    data: data.map(item => item.totalAlunosMatriculados),
                    backgroundColor: "rgba(78, 121, 167, 0.2)",
                    borderColor: "#4e79a7",
                    pointBackgroundColor: "#4e79a7"
                },
                {
                    label: "Novos Alunos",
                    data: data.map(item => item.novosAlunosUltimos30Dias),
                    backgroundColor: "rgba(88, 183, 87, 0.2)",
                    borderColor: "#59a14f",
                    pointBackgroundColor: "#59a14f"
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                title: { display: true, text: 'Comparativo entre Cursos' }
            }
        }
    });

    // Gráfico de Pizza: Distribuição de Matrículas por Curso
    new Chart(document.getElementById("chartDistribuicaoMatriculas"), {
        type: "pie",
        data: {
            labels: cursos,
            datasets: [{
                label: "Distribuição de Matrículas",
                data: data.map(item => item.totalAlunosMatriculados),
                backgroundColor: [
                    "#4e79a7", "#f28e2b", "#e15759", "#76b7b2", "#59a14f", "#edc948"
                ]
            }]
        }
    });
});
