document.addEventListener("DOMContentLoaded", async () => {
    const tabela = document.getElementById("tabelaCursos");
    const mensagem = document.getElementById("listaCursosMsg");

    const API_URL = "/api/cursos";

    function mostrarMensagem(texto, sucesso = true) {
        if (!mensagem) return;
        mensagem.textContent = texto;
        mensagem.style.color = sucesso ? "green" : "red";
    }

    function preencherTabela(cursos) {
        if (!tabela) return;

        if (!cursos || cursos.length === 0) {
            tabela.innerHTML = `<tr><td colspan="4" style="text-align:center;">Nenhum curso encontrado.</td></tr>`;
            return;
        }

        tabela.innerHTML = cursos.map(curso => `
            <tr>
                <td>${curso.id}</td>
                <td>${curso.nome}</td>
                <td>${curso.descricao}</td>
                <td>${curso.cargaHoraria}</td>
            </tr>
        `).join('');
    }

    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error("Erro ao buscar cursos.");
        const cursos = await response.json();
        preencherTabela(cursos);
        mostrarMensagem("Cursos listados com sucesso!");
    } catch (err) {
        mostrarMensagem(err.message, false);
        if (tabela) tabela.innerHTML = "";
    }
});
