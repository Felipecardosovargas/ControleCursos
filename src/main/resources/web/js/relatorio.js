async function carregarRelatorio() {
    try {
        const response = await fetch("/api/relatorios/engajamento");
        if (!response.ok) throw new Error("Erro HTTP: " + response.status);

        const data = await response.json(); // array de objetos

        const tbody = document.getElementById("relatorio-body");
        tbody.innerHTML = ""; // limpa linhas antigas

        data.forEach(curso => {
            // Cria uma linha para cada métrica do curso
            const linhas = [
                {
                    objetivo: `Relatório do curso: ${curso.cursoNome}`,
                    metrica: "Total de alunos",
                    valor: curso.totalAlunosMatriculados
                },
                {
                    objetivo: `Relatório do curso: ${curso.cursoNome}`,
                    metrica: "Média de idade",
                    valor: curso.mediaIdadeAlunos.toFixed(1) + " anos"
                },
                {
                    objetivo: `Relatório do curso: ${curso.cursoNome}`,
                    metrica: "Matrículas recentes",
                    valor: curso.novosAlunosUltimos30Dias
                }
            ];

            linhas.forEach(item => {
                const row = document.createElement("tr");
                row.innerHTML = `
          <td>${item.objetivo}</td>
          <td>${item.metrica}</td>
          <td>${item.valor}</td>
        `;
                tbody.appendChild(row);
            });
        });

    } catch (error) {
        console.error("Erro ao carregar relatório:", error);
    }
}

carregarRelatorio();
