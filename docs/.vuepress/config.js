const baseHref = !!process.env.BASE_HREF ? process.env.BASE_HREF : '/';
module.exports = {
    head: [
        ['link', { rel: 'icon', href: '/favicon.ico' }]
    ],
    base: baseHref,
    themeConfig: {
        repo: 'daggerok/sonar-quality-gates-build-breaker',
    },
    markdown: {
        lineNumbers: true,
        extendMarkdown: md => {
            md.use(require('markdown-it-vuepress-code-snippet-enhanced'))
        }
    },
};
