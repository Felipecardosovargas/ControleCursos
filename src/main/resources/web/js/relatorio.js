// relatorio.js

const ENDPOINT = "http://localhost:8080/api/relatorios/engajamento-cursos";

/**
 * Busca os dados de engajamento de cursos da API.
 * Exibe mensagens de carregamento e erro no DOM, se necessário.
 *
 * @returns {Promise<Array|null>} Array de dados ou null em caso de erro
 */
export async function buscarDadosEngajamento() {
    const loadingEl = document.getElementById('carregando');
    const errorEl = document.getElementById('mensagem-erro');

    if (loadingEl) loadingEl.style.display = 'block';
    if (errorEl) errorEl.textContent = '';

    try {
        const response = await fetch(ENDPOINT);

        if (!response.ok) {
            throw new Error(`Erro HTTP ${response.status} - ${response.statusText}`);
        }

        const json = await response.json();

        if (!json.success || !json.data) {
            throw new Error(json.message || "Dados inválidos recebidos da API.");
        }

        return json.data;

    } catch (error) {
        console.error("[Relatório] Falha ao buscar dados:", error);
        if (errorEl) {
            errorEl.textContent = `Erro ao carregar dados do relatório: ${error.message}`;
        }
        return null;

    } finally {
        if (loadingEl) loadingEl.style.display = 'none';
    }
}
