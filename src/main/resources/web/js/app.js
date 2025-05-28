const apiUrl = "http://localhost:8080/api/alunos";

// Define o max da data de nascimento como hoje
document.addEventListener("DOMContentLoaded", () => {
    const hoje = new Date().toISOString().split("T")[0];
    document.getElementById("dataNascimento").setAttribute("max", hoje);
});

// Mensagens de feedback
const formMsg = document.getElementById("formAlunoMsg");
const listaMsg = document.getElementById("listaAlunosMsg");

// Função para mostrar mensagens
function mostrarMensagem(msg, elemento) {
    elemento.textContent = msg;
    setTimeout(() => elemento.textContent = "", 4000);
}

// Carregar alunos
async function carregarAlunos() {
    try {
        const res = await fetch(apiUrl);
        const alunos = await res.json();

        const tbody = document.querySelector("#tabelaAlunos tbody");
        tbody.innerHTML = "";

        if (alunos.length === 0) {
            mostrarMensagem("Nenhum aluno encontrado.", listaMsg);
            return;
        }

        alunos.forEach(aluno => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${aluno.id}</td>
                <td>${aluno.nome}</td>
                <td>${aluno.email}</td>
                <td>${aluno.dataNascimento}</td>
                <td>
                    <button class="btn btn-danger" onclick="excluirAluno(${aluno.id})" aria-label="Excluir aluno ${aluno.nome}">Excluir</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        mostrarMensagem("Erro ao carregar alunos.", listaMsg);
        console.error(err);
    }
}

// Cadastrar novo aluno
document.getElementById("cadastroAlunoForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const aluno = {
        nome: document.getElementById("nome").value.trim(),
        email: document.getElementById("email").value.trim(),
        dataNascimento: document.getElementById("dataNascimento").value
    };

    if (!aluno.nome || !aluno.email || !aluno.dataNascimento) {
        mostrarMensagem("Preencha todos os campos corretamente.", formMsg);
        return;
    }

    try {
        const res = await fetch(apiUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(aluno)
        });

        if (res.ok) {
            mostrarMensagem("Aluno cadastrado com sucesso!", formMsg);
            e.target.reset();
            carregarAlunos();
        } else {
            const erro = await res.text();
            mostrarMensagem(`Erro ao cadastrar aluno: ${erro}`, formMsg);
        }
    } catch (err) {
        mostrarMensagem("Erro ao se comunicar com o servidor.", formMsg);
        console.error(err);
    }
});

// Botão "Listar Alunos"
document.getElementById("btnListarAlunos").addEventListener("click", carregarAlunos);

// Função para excluir aluno
async function excluirAluno(id) {
    if (!confirm("Tem certeza que deseja excluir este aluno?")) return;

    try {
        const res = await fetch(`${apiUrl}/${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            mostrarMensagem("Aluno excluído com sucesso!", listaMsg);
            carregarAlunos();
        } else {
            mostrarMensagem("Erro ao excluir aluno.", listaMsg);
        }
    } catch (err) {
        mostrarMensagem("Erro ao se comunicar com o servidor.", listaMsg);
        console.error(err);
    }
}
