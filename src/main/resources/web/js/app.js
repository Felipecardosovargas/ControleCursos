const API_BASE_URL = 'http://localhost:8080/api'; // Ensure this matches your server port

document.addEventListener('DOMContentLoaded', () => {
    const cadastroAlunoForm = document.getElementById('cadastroAlunoForm');
    const btnListarAlunos = document.getElementById('btnListarAlunos');
    const tabelaAlunosBody = document.querySelector('#tabelaAlunos tbody');
    const alunoMessage = document.getElementById('alunoMessage');

    if (cadastroAlunoForm) {
        cadastroAlunoForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const nome = document.getElementById('nome').value;
            const email = document.getElementById('email').value;
            const dataNascimento = document.getElementById('dataNascimento').value;

            try {
                const response = await fetch(`${API_BASE_URL}/alunos`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ nome, email, dataNascimento })
                });
                const result = await response.json();
                if (response.ok) {
                    alunoMessage.textContent = `Aluno ${result.nome} cadastrado com ID: ${result.id}`;
                    alunoMessage.className = 'success';
                    cadastroAlunoForm.reset();
                    if (btnListarAlunos) listarAlunos(); // Refresh list
                } else {
                    alunoMessage.textContent = `Erro: ${result.error || response.statusText}`;
                    alunoMessage.className = 'error';
                }
            } catch (error) {
                alunoMessage.textContent = `Erro de conexão: ${error.message}`;
                alunoMessage.className = 'error';
                console.error("Erro ao cadastrar aluno:", error);
            }
        });
    }

    if (btnListarAlunos) {
        btnListarAlunos.addEventListener('click', listarAlunos);
        listarAlunos(); // Load initially if on alunos.html
    }

    async function listarAlunos() {
        if (!tabelaAlunosBody) return;
        try {
            const response = await fetch(`${API_BASE_URL}/alunos`);
            if (!response.ok) {
                const errorResult = await response.json().catch(() => ({ error: response.statusText }));
                console.error("Erro ao listar alunos:", errorResult.error);
                tabelaAlunosBody.innerHTML = `<tr><td colspan="5">Erro ao carregar alunos: ${errorResult.error}</td></tr>`;
                return;
            }
            const alunos = await response.json();
            tabelaAlunosBody.innerHTML = ''; // Clear existing rows
            if (alunos.length === 0) {
                tabelaAlunosBody.innerHTML = `<tr><td colspan="5">Nenhum aluno cadastrado.</td></tr>`;
            } else {
                alunos.forEach(aluno => {
                    const row = tabelaAlunosBody.insertRow();
                    row.innerHTML = `
                        <td>${aluno.id}</td>
                        <td>${aluno.nome}</td>
                        <td>${aluno.email}</td>
                        <td>${aluno.dataNascimento}</td>
                        <td>
                            <button onclick="editarAluno(${aluno.id})">Editar</button>
                            <button onclick="deletarAluno(${aluno.id}, '${aluno.nome}')">Deletar</button>
                        </td>
                    `;
                });
            }
        } catch (error) {
            console.error("Erro ao listar alunos:", error);
            tabelaAlunosBody.innerHTML = `<tr><td colspan="5">Erro de conexão ao carregar alunos.</td></tr>`;
        }
    }
});

// Placeholder functions for edit/delete - these would open modals or redirect
async function editarAluno(id) {
    // Fetch aluno data, populate a form, then send PUT request
    alert(`Editar aluno ID: ${id} (implementação pendente)`);
    // Example: const nome = prompt("Novo nome:", aluno.nome);
    // if (nome) { /* make PUT request */ }
}

async function deletarAluno(id, nome) {
    if (confirm(`Tem certeza que deseja deletar o aluno ${nome} (ID: ${id})?`)) {
        try {
            const response = await fetch(`${API_BASE_URL}/alunos/${id}`, { method: 'DELETE' });
            const result = await response.json().catch(() => ({})); // Handle empty response for 204

            if (response.ok || response.status === 204) {
                alert(`Aluno ${nome} deletado com sucesso.`);
                document.querySelector('#btnListarAlunos')?.click(); // Refresh list
            } else {
                alert(`Erro ao deletar aluno: ${result.error || response.statusText}`);
            }
        } catch (error) {
            alert(`Erro de conexão ao deletar aluno: ${error.message}`);
            console.error("Erro ao deletar aluno:", error);
        }
    }
}

// Add similar JS functions for Cursos and Matrículas on their respective pages.