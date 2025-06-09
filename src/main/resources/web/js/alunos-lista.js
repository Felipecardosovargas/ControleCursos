document.addEventListener("DOMContentLoaded", () => {
    const tabela = document.getElementById("tabelaAlunos");
    const mensagem = document.getElementById("listaAlunosMsg");

    async function carregarAlunos() {
        try {
            const response = await fetch("http://localhost:8080/api/alunos");

            if (!response.ok) {
                throw new Error(`Erro ao buscar alunos: ${response.status}`);
            }

            const alunos = await response.json();

            if (alunos.length === 0) {
                mensagem.textContent = "Nenhum aluno cadastrado.";
                return;
            }

            alunos.forEach(aluno => {
                const tr = document.createElement("tr");

                tr.innerHTML = `
                    <td>${aluno.id}</td>
                    <td>${aluno.nome}</td>
                    <td>${aluno.email}</td>
                    <td>${aluno.dataNascimento || "Não informado"}</td>
                    <td>
                        <button class="btn btn-secondary" onclick="editarAluno(${aluno.id})">Editar</button>
                        <button class="btn btn-danger" onclick="excluirAluno(${aluno.id})">Excluir</button>
                    </td>
                `;

                tabela.appendChild(tr);
            });
        } catch (erro) {
            console.error(erro);
            mensagem.textContent = "Erro ao carregar a lista de alunos.";
        }
    }

    carregarAlunos();
});

// Funções fictícias para ações futuras
function editarAluno(id) {
    alert(`Funcionalidade de edição para o aluno ID ${id} em desenvolvimento.`);
}

function excluirAluno(id) {
    alert(`Funcionalidade de exclusão para o aluno ID ${id} em desenvolvimento.`);
}
