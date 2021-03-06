const baseHref = !!process.env.BASE_HREF ? process.env.BASE_HREF : '/';
module.exports = {
    title: 'SonarQube Quality Gates Build Breaker',
    description: 'SonarQube Build Breaker tool, designed to fail CI b=pipelines builds if SonarQube analysis not passed',
    head: [
        ['link', { rel: 'icon', href: '/favicon.ico' }]
    ],
    base: baseHref,
    themeConfig: {
        repo: 'daggerok/sonar-quality-gates-build-breaker',
        nav: [
            { text: 'Home', link: '/', },
            { text: 'Write custom maven plugin', link: '/write-custom-maven-plugin/', },
            { text: 'Release and deploy', link: '/release-and-deploy/', },
        ],
        sidebar: [
            '/',
            '/write-custom-maven-plugin/',
            '/release-and-deploy/',
        ]
    },
    markdown: {
        lineNumbers: true,
        extendMarkdown: md => {
            md.use(require('markdown-it-vuepress-code-snippet-enhanced'))
        }
    },
    plugins: [
        'tag',
        'category',
        '@vuepress/back-to-top',
        '@vuepress/last-updated',
        '@vuepress/medium-zoom',
        '@vuepress/nprogress',
        '@vuepress/nprogress',
    ],
};
