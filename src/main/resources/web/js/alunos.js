const apiUrl = "http://localhost:8080/api/alunos";

// Aguarda o DOM estar pronto
document.addEventListener("DOMContentLoaded", () => {
    definirDataMaxima();
    configurarFormulario();
    configurarBotaoListar();
    carregarAlunos(); // opcional: carregar alunos ao abrir
});

// Define o máximo da data de nascimento como hoje
function definirDataMaxima() {
    const hoje = new Date().toISOString().split("T")[0];
    const dataInput = document.getElementById("dataNascimento");
    if (dataInput) {
        dataInput.setAttribute("max", hoje);
    }
}

// Mostra mensagens de feedback
function mostrarMensagem(msg, elemento, tipo = "sucesso") {
    if (!elemento) return;

    elemento.textContent = msg;
    elemento.classList.remove("error", "sucesso");
    elemento.classList.add(tipo === "erro" ? "error" : "sucesso");
    elemento.style.display = "block";

    setTimeout(() => {
        elemento.style.display = "none";
        elemento.textContent = "";
    }, 4000);
}

// Configura o formulário de cadastro
function configurarFormulario() {
    const form = document.getElementById("cadastroAlunoForm");
    const formMsg = document.getElementById("formAlunoMsg");

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const aluno = {
            nome: document.getElementById("nome").value.trim(),
            email: document.getElementById("email").value.trim(),
            dataNascimento: document.getElementById("dataNascimento").value
        };

        // Validação
        if (!aluno.nome || !aluno.email || !aluno.dataNascimento) {
            mostrarMensagem("Preencha todos os campos corretamente.", formMsg, "erro");
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
                form.reset();
                carregarAlunos();
            } else {
                const erro = await res.text();
                mostrarMensagem(`Erro ao cadastrar aluno: ${erro}`, formMsg, "erro");
            }
        } catch (err) {
            console.error(err);
            mostrarMensagem("Erro ao se comunicar com o servidor.", formMsg, "erro");
        }
    });
}

// Configura o botão "Listar Alunos", se existir
function configurarBotaoListar() {
    const btnListar = document.getElementById("btnListarAlunos");
    if (btnListar) {
        btnListar.addEventListener("click", carregarAlunos);
    }
}

// Carrega a lista de alunos e popula a tabela
async function carregarAlunos() {
    const listaMsg = document.getElementById("listaAlunosMsg");
    const tbody = document.querySelector("#tabelaAlunos tbody");

    if (!tbody) return;

    try {
        const res = await fetch(apiUrl);
        const alunos = await res.json();

        tbody.innerHTML = "";

        if (!alunos.length) {
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
        console.error(err);
        mostrarMensagem("Erro ao carregar alunos.", listaMsg, "erro");
    }
}

// Exclui um aluno
async function excluirAluno(id) {
    const listaMsg = document.getElementById("listaAlunosMsg");
    if (!confirm("Tem certeza que deseja excluir este aluno?")) return;

    try {
        const res = await fetch(`${apiUrl}/${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            mostrarMensagem("Aluno excluído com sucesso!", listaMsg);
            carregarAlunos();
        } else {
            mostrarMensagem("Erro ao excluir aluno.", listaMsg, "erro");
        }
    } catch (err) {
        console.error(err);
        mostrarMensagem("Erro ao se comunicar com o servidor.", listaMsg, "erro");
    }
}
