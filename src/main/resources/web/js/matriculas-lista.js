document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.getElementById("tabelaMatriculasBody");

    fetch("/api/matriculas")
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao carregar dados.");
            }
            return response.json();
        })
        .then(result => {
            const matriculas = result.data;
            renderizarTabela(matriculas);
        })
        .catch(error => {
            tbody.innerHTML = `
                <tr><td colspan="6" style="text-align: center;">Erro ao carregar dados.</td></tr>
            `;
            console.error(error);
        });
});

function renderizarTabela(matriculas) {
    const tbody = document.getElementById("tabelaMatriculasBody");
    tbody.innerHTML = "";

    matriculas.forEach(matricula => {
        const tr = document.createElement("tr");

        const dataFormatada = new Date(
            matricula.dataMatricula[0],
            matricula.dataMatricula[1] - 1,
            matricula.dataMatricula[2]
        ).toLocaleDateString("pt-BR");

        const status = matricula.cancelada ? "Cancelada" : "Ativa";
        const statusClasse = matricula.cancelada ? "status-cancelada" : "status-ativa";

        tr.innerHTML = `
            <td>${matricula.id}</td>
            <td>${matricula.alunoNome}</td>
            <td>${matricula.cursoNome}</td>
            <td>${dataFormatada}</td>
            <td><span class="status ${statusClasse}">${status}</span></td>
            <td>
                ${!matricula.cancelada
            ? `<button class="btn-cancelar" onclick="cancelarMatricula(${matricula.id})">Cancelar</button>`
            : ""}
            </td>
        `;

        tbody.appendChild(tr);
    });
}

function cancelarMatricula(id) {
    fetch(`/api/matriculas/${id}/cancelar`, {
        method: "POST"
    }).then(() => location.reload());
}
